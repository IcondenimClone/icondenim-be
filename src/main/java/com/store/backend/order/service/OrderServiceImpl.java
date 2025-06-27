package com.store.backend.order.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.store.backend.cart.entity.CartEntity;
import com.store.backend.cart.repository.CartRepository;
import com.store.backend.exception.ConflictException;
import com.store.backend.exception.NotFoundException;
import com.store.backend.order.entity.OrderEntity;
import com.store.backend.order.entity.OrderItemEntity;
import com.store.backend.order.enums.OrderStatus;
import com.store.backend.order.repository.OrderRepository;
import com.store.backend.order.request.PlaceOrderRequest;
import com.store.backend.redis.RedisService;
import com.store.backend.smtp.EmailService;
import com.store.backend.user.UserEntity;
import com.store.backend.user.UserRepository;
import com.store.backend.variant.VariantEntity;
import com.store.backend.variant.VariantRepository;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {
  OrderRepository orderRepository;
  UserRepository userRepository;
  VariantRepository variantRepository;
  CartRepository cartRepository;
  RedisService redisService;
  EmailService emailService;

  @Override
  @Transactional
  public OrderEntity placeOrder(String userId, PlaceOrderRequest request) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    CartEntity cart = user.getCart();
    if (cart == null || cart.getItems().isEmpty()) {
      throw new ConflictException("Giỏ hàng trống");
    }

    String shippingAddress = mergeAddress(request.getAddress(), request.getCommune(), request.getDistrict(),
        request.getProvince());

    OrderEntity newOrder = OrderEntity.builder().user(user).fullName(request.getFullName())
        .phoneNumber(request.getPhoneNumber()).shippingAddress(shippingAddress)
        .paymentMethod(request.getPaymentMethod()).note(request.getNote()).build();

    List<OrderItemEntity> items = getOrderItems(cart, newOrder);
    newOrder.setItems(new HashSet<>(items));

    int totalQuantity = items.stream().mapToInt(OrderItemEntity::getQuantity).sum();
    BigDecimal totalPrice = items.stream().map(OrderItemEntity::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    newOrder.setTotalQuantity(totalQuantity);
    newOrder.setTotalPrice(totalPrice);

    cart.clearCart();
    cartRepository.save(cart);

    OrderEntity savedOrder = orderRepository.save(newOrder);
    sendEmail(savedOrder, user.getEmail());
    return savedOrder;
  }

  @Override
  public void confirmOrder(String token) {
    String redisKey = redisService.setKey(token, ":order:");
    String id = redisService.getString(redisKey);
    OrderEntity order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng hoặc token không hợp lệ"));
    if (order.getStatus() != OrderStatus.WAITING) {
      throw new ConflictException("Đơn hàng đã được xử lý");
    }

    order.setStatus(OrderStatus.CONFIRMED);
    orderRepository.save(order);

    redisService.deleteString(redisKey);
  }

  @Override
  public void sendEmail(OrderEntity savedOrder, String to) {
    String confirmToken = UUID.randomUUID().toString();
    String confirmLink = "http://localhost:2004/icondenim-be/orders/confirm?token=" + confirmToken;

    emailService.sendOrderEmail(to, "Xác nhận Đơn đặt hàng tại Icondenim", savedOrder, confirmLink);

    String redisKey = redisService.setKey(confirmToken, ":order:");
    redisService.saveString(redisKey, savedOrder.getId(), 1, TimeUnit.DAYS);
  }

  private List<OrderItemEntity> getOrderItems(CartEntity cart, OrderEntity newOrder) {
    return cart.getItems().stream().map(item -> {
      VariantEntity variant = item.getVariant();
      variant.setQuantityPurchase(variant.getQuantityPurchase() + item.getQuantity());
      variant.setStock();
      variantRepository.save(variant);

      OrderItemEntity newOrderItem = OrderItemEntity.builder().order(newOrder).variant(variant)
          .quantity(item.getQuantity()).build();
      newOrderItem.setUnitPrice();
      newOrderItem.setTotalPrice();
      return newOrderItem;
    }).toList();
  }

  @Override
  public String mergeAddress(String address, String commune, String district, String province) {
    return address + ", " + commune + ", " + district + ", " + province;
  }
}

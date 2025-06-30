package com.store.backend.order.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.store.backend.cart.entity.CartEntity;
import com.store.backend.cart.repository.CartRepository;
import com.store.backend.exception.ConflictException;
import com.store.backend.exception.NotFoundException;
import com.store.backend.order.dto.OrderEmailDto;
import com.store.backend.order.entity.OrderEntity;
import com.store.backend.order.entity.OrderItemEntity;
import com.store.backend.order.enums.OrderStatus;
import com.store.backend.order.repository.OrderRepository;
import com.store.backend.order.request.PlaceOrderRequest;
import com.store.backend.rabbitmq.PublishMessage;
import com.store.backend.redis.RedisService;
import com.store.backend.user.UserEntity;
import com.store.backend.user.UserRepository;
import com.store.backend.variant.VariantEntity;
import com.store.backend.variant.VariantRepository;
import com.store.backend.voucher.VoucherEntity;
import com.store.backend.voucher.VoucherRepository;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

  PublishMessage publishMessage;
  OrderRepository orderRepository;
  UserRepository userRepository;
  VariantRepository variantRepository;
  CartRepository cartRepository;
  RedisService redisService;
  VoucherRepository voucherRepository;

  @Override
  @Transactional
  public OrderEntity placeOrder(String userId, PlaceOrderRequest request) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));

    VoucherEntity voucher = null;
    if (request.getVoucher() != null) {
      voucher = voucherRepository.findByCodeIgnoreCase(request.getVoucher())
          .orElseThrow(() -> new NotFoundException("Không tìm thấy voucher"));
    }

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
    BigDecimal discount = BigDecimal.ZERO;

    if (voucher != null) {
      if (!voucher.isValid()) {
        throw new ConflictException("Voucher không hợp lệ");
      }
      if (voucher.getMinimumOrderAmount() == null
          || (voucher.getMinimumOrderAmount() != null && totalPrice.compareTo(voucher.getMinimumOrderAmount()) >= 0)) {
        discount = voucher.getDiscountPercent() != null
            ? totalPrice.multiply(BigDecimal.valueOf(voucher.getDiscountPercent())).divide(BigDecimal.valueOf(100))
            : voucher.getDiscountAmount();
        if (voucher.getMaximumDiscount() != null) {
          discount = discount.compareTo(voucher.getMaximumDiscount()) <= 0 ? discount : voucher.getMaximumDiscount();
        }
        if (voucher.getQuantity() != null) {
          voucher.setUsed(voucher.getUsed() + 1);
          voucher.setStock();
          voucherRepository.save(voucher);
        }
      }
    }

    newOrder.setTotalQuantity(totalQuantity);
    newOrder.setTotalPrice(totalPrice);
    newOrder.setDiscount(discount);
    newOrder.setTotalBill();

    cart.clearCart();
    cartRepository.save(cart);

    OrderEntity savedOrder = orderRepository.save(newOrder);

    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        sendEmail(savedOrder, user.getEmail());
      }
    });
    return savedOrder;
  }

  @Override
  public void confirmOrder(String token) {
    String redisKey = redisService.setKey(token, ":order:");
    String id = redisService.getString(redisKey);
    OrderEntity order = orderRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng hoặc token không hợp lệ"));
    if (order.getStatus() != OrderStatus.WAITING) {
      throw new ConflictException("Đơn hàng đã được xử lý");
    }

    order.setStatus(OrderStatus.PENDING);
    orderRepository.save(order);

    redisService.deleteString(redisKey);
  }

  @Override
  public void sendEmail(OrderEntity savedOrder, String to) {
    String confirmToken = UUID.randomUUID().toString();
    String confirmLink = "http://localhost:2004/icondenim-be/orders/confirm?token=" + confirmToken;

    OrderEmailDto message = OrderEmailDto.builder().to(to).subject("Xác nhận Đơn đặt hàng tại Icondenim")
        .orderId(savedOrder.getId()).confirmLink(confirmLink).build();
    publishMessage.sendOrderEmail(message);

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

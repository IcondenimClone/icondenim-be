package com.store.backend.order.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.store.backend.cart.entity.CartEntity;
import com.store.backend.cart.repository.CartRepository;
import com.store.backend.exception.ConflictException;
import com.store.backend.exception.NotFoundException;
import com.store.backend.order.entity.OrderEntity;
import com.store.backend.order.entity.OrderItemEntity;
import com.store.backend.order.repository.OrderRepository;
import com.store.backend.order.request.PlaceOrderRequest;
import com.store.backend.user.UserEntity;
import com.store.backend.user.UserRepository;
import com.store.backend.variant.VariantEntity;
import com.store.backend.variant.VariantRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final VariantRepository variantRepository;
  private final CartRepository cartRepository;

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
    return orderRepository.save(newOrder);
  }

  private List<OrderItemEntity> getOrderItems(CartEntity cart, OrderEntity newOrder) {
    return cart.getItems().stream().map(item -> {
      VariantEntity variant = item.getVariant();
      variant.setQuantityPurchase(variant.getQuantityPurchase() + item.getQuantity());
      variant.setStock();
      variantRepository.save(variant);

      OrderItemEntity newOrderItem = OrderItemEntity.builder().order(newOrder).variant(variant)
          .quantity(item.getQuantity()).unitPrice(item.getUnitPrice()).build();
      newOrderItem.setTotalPrice();
      return newOrderItem;
    }).toList();
  }

  private String mergeAddress(String address, String commune, String district, String province) {
    return address + ", " + commune + ", " + district + ", " + province;
  }
}

package com.store.backend.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.store.backend.common.BaseEntity;
import com.store.backend.order.enums.OrderStatus;
import com.store.backend.order.enums.PaymentMethod;
import com.store.backend.order.enums.PaymentStatus;
import com.store.backend.user.UserEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders")
public class OrderEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, length = 36)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @Column(nullable = false)
  @Builder.Default
  private boolean guestOrder = false;

  @Column(nullable = false, length = 100)
  private String fullName;

  @Column(nullable = false, length = 11)
  private String phoneNumber;

  @Column(nullable = false)
  private String shippingAddress;

  @Column(nullable = false)
  private int totalQuantity;

  @Column(nullable = false)
  private BigDecimal totalPrice;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private OrderStatus status = OrderStatus.WAITING;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentMethod paymentMethod;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

  @Column
  private LocalDateTime paidAt;

  @Column
  private String note;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<OrderItemEntity> items = new HashSet<>();
}

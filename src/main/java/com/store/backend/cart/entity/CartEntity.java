package com.store.backend.cart.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.store.backend.common.BaseEntity;
import com.store.backend.user.UserEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "carts", uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
public class CartEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  @Builder.Default
  private BigDecimal totalPrice = BigDecimal.ZERO;

  @Column(nullable = false)
  @Builder.Default
  private int totalQuantity = 0;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<CartItemEntity> items = new HashSet<>();

  public void addItem(CartItemEntity item) {
    this.items.add(item);
    item.setCart(this);
    this.totalQuantity += item.getQuantity();
    this.totalPrice = this.totalPrice.add(item.getTotalPrice());
  }
}

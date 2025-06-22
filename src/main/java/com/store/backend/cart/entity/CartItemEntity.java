package com.store.backend.cart.entity;

import java.math.BigDecimal;

import com.store.backend.common.BaseEntity;
import com.store.backend.variant.VariantEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "cart_items")
public class CartItemEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private int quantity;

  @Column(nullable = false)
  private BigDecimal unitPrice;

  @Column(nullable = false)
  private BigDecimal totalPrice;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cart_id")
  private CartEntity cart;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "variant_id")
  private VariantEntity variant;

  public void setUnitPrice() {
    if (this.variant.getProduct().getSalePrice() != null) {
      this.unitPrice = this.variant.getProduct().getSalePrice();
    } else {
      this.unitPrice = this.variant.getProduct().getPrice();
    }
  }

  public void setTotalPrice() {
    this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
  }
}

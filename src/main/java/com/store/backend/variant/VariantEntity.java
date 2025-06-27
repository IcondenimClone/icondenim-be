package com.store.backend.variant;

import java.util.HashSet;
import java.util.Set;

import com.store.backend.cart.entity.CartItemEntity;
import com.store.backend.color.ColorEntity;
import com.store.backend.common.BaseEntity;
import com.store.backend.product.ProductEntity;
import com.store.backend.size.SizeEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "variants", uniqueConstraints = @UniqueConstraint(columnNames = "sku", name = "variants_sku_key"))
public class VariantEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, length = 36)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private ProductEntity product;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "color_id")
  private ColorEntity color;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "size_id")
  private SizeEntity size;

  @Column(nullable = false, length = 50)
  private String sku;

  @Column(nullable = false)
  private int quantity;

  @Column(nullable = false)
  @Builder.Default
  private int quantityPurchase = 0;

  @Column(nullable = false)
  private int stock;

  @Column(nullable = false)
  private boolean inStock;

  @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<CartItemEntity> cartItems = new HashSet<>();

  public void setStock() {
    this.stock = this.quantity - this.quantityPurchase;
    this.inStock = this.stock >= 5;
  }
}

package com.store.backend.variant;

import com.store.backend.color.ColorEntity;
import com.store.backend.common.BaseEntity;
import com.store.backend.product.ProductEntity;
import com.store.backend.size.SizeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "variants", uniqueConstraints = @UniqueConstraint(columnNames = "sku"))
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
  @Builder.Default
  private int quantity = 200;

  @Column(nullable = false)
  @Builder.Default
  private int quantityPurchase = 0;

  @Column(nullable = false)
  @Builder.Default
  private int stock = 200;

  @Column(nullable = false)
  @Builder.Default
  private boolean inStock = true;

  public void setStock() {
    this.stock = this.quantity - this.quantityPurchase;
    if (this.stock < 5) {
      this.inStock = false;
    }
  }
}

package com.store.backend.product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.store.backend.category.CategoryEntity;
import com.store.backend.common.BaseEntity;
import com.store.backend.image.ImageEntity;
import com.store.backend.variant.VariantEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products", uniqueConstraints = @UniqueConstraint(columnNames = "slug"))
public class ProductEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, length = 36)
  private String id;

  @Column(nullable = false, length = 255)
  private String title;

  @Column(nullable = false, length = 255)
  private String slug;

  @Lob
  @Column(columnDefinition = "text")
  private String description;

  @Column(nullable = false)
  private BigDecimal price;

  @Column(nullable = false)
  @Builder.Default
  private boolean saleProduct = false;

  @Column
  private BigDecimal salePrice;

  @Column
  private LocalDate startSale;

  @Column
  private LocalDate endSale;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "product_categories", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
  @Builder.Default
  private Set<CategoryEntity> categories = new HashSet<>();

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<ImageEntity> images = new ArrayList<>();

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<VariantEntity> variants = new HashSet<>();
}

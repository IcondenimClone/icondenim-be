package com.store.backend.category;

import java.util.HashSet;
import java.util.Set;

import com.store.backend.common.BaseEntity;
import com.store.backend.product.ProductEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = "slug"))
public class CategoryEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, length = 36)
  private String id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, length = 100)
  private String slug;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "category_parents", joinColumns = @JoinColumn(name = "child_id"), inverseJoinColumns = @JoinColumn(name = "parent_id"))
  @Builder.Default
  private Set<CategoryEntity> parents = new HashSet<>();

  @ManyToMany(mappedBy = "parents", fetch = FetchType.LAZY)
  @Builder.Default
  private Set<CategoryEntity> children = new HashSet<>();

  @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
  @Builder.Default
  private Set<ProductEntity> products = new HashSet<>();
}

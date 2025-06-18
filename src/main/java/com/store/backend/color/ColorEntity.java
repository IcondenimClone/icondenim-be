package com.store.backend.color;

import java.util.HashSet;
import java.util.Set;

import com.store.backend.common.BaseEntity;
import com.store.backend.variant.VariantEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "colors")
public class ColorEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, length = 36)
  private String id;

  @Column(nullable = false, length = 100)
  private String name;

  @OneToMany(mappedBy = "color", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<VariantEntity> variants = new HashSet<>();
}

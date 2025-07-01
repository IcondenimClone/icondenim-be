package com.store.backend.favorite;

import com.store.backend.product.ProductEntity;
import com.store.backend.user.UserEntity;

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
@Table(name = "favorites", uniqueConstraints = {
  @UniqueConstraint(columnNames = "product_id", name = "favorites_product_id_key"),
  @UniqueConstraint(columnNames = "user_id", name = "favorites_user_id_key")
})
public class FavoriteEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, length = 36)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private ProductEntity product;
}

package com.store.backend.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.store.backend.order.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {
  @EntityGraph(attributePaths = {
      "items",
      "items.variant",
      "items.variant.product",
      "items.variant.product.images",
      "items.variant.size",
      "items.variant.color"
  })
  @NonNull
  Optional<OrderEntity> findById(@NonNull String id);
}

package com.store.backend.product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, String> {
  boolean existsBySlug(String slug);

  Optional<ProductEntity> findBySlug(String slug);
}

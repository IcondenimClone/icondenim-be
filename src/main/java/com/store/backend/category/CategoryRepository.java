package com.store.backend.category;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, String> {
  boolean existsBySlug(String slug);

  Optional<CategoryEntity> findBySlug(String slug);
}

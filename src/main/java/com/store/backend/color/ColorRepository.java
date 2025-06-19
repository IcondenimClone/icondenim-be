package com.store.backend.color;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ColorRepository extends JpaRepository<ColorEntity, String> {
  boolean existsByNameIgnoreCase(String name);
}

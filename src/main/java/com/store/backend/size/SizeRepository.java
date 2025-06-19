package com.store.backend.size;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SizeRepository extends JpaRepository<SizeEntity, String> {
  boolean existsByNameIgnoreCase(String name);
}

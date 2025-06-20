package com.store.backend.variant;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VariantRepository extends JpaRepository<VariantEntity, String> {
  boolean existsBySkuIgnoreCase(String sku);

  boolean existsByProductIdAndColorIdAndSizeId(String productId, String colorId, String sizeId);

  @Query("SELECT v FROM VariantEntity v JOIN FETCH v.product WHERE v.id = :id")
  Optional<VariantEntity> findByIdWithProduct(@Param("id") String id);
}

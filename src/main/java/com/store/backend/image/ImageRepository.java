package com.store.backend.image;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageRepository extends JpaRepository<ImageEntity, String> {
  @Query("SELECT i FROM ImageEntity i JOIN FETCH i.product WHERE i.id = :id")
  Optional<ImageEntity> findByIdWithProduct(@Param("id") String id);

  boolean existsByProductIdAndColorIdAndSortOrder(String productId, String colorId, int sortOrder);

  @Query("SELECT COALESCE(MAX(i.sortOrder), 0) FROM ImageEntity i WHERE i.product.id = :productId AND i.color.id = :colorId")
  int findMaxSortOrderByProductIdAndColorId(@Param("productId") String productId, @Param("colorId") String colorId);

  boolean existsByProductIdAndColorIdAndThumbnailImage(String productId, String colorId, boolean thumbnailImage);

  Optional<ImageEntity> findByProductIdAndColorIdAndThumbnailImage(String productId, String colorId,
      boolean thumbnailImage);

  boolean existsByProductIdAndColorIdAndThumbnailImageIsTrueAndIdNot(String productId, String colorId,
      String id);

  Optional<ImageEntity> findTopByProductIdAndColorIdAndIdNotOrderByCreatedAtDesc(String productId, String colorId,
      String id);
}

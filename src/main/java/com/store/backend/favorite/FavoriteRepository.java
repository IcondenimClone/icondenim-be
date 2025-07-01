package com.store.backend.favorite;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<FavoriteEntity, String> {
  boolean existsByUserIdAndProductId(String userId, String productId);

  void deleteByUserIdAndProductId(String userId, String productId);

  List<FavoriteEntity> findAllByUserId(String userId);
}

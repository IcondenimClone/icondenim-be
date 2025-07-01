package com.store.backend.favorite;

import java.util.List;

import com.store.backend.product.response.ProductResponse;

public interface FavoriteService {
  void toggleFavorite(String userId, String productId);

  List<ProductResponse> getUserFavorites(String userId);
}

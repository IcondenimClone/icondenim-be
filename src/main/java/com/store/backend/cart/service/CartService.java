package com.store.backend.cart.service;

import com.store.backend.cart.entity.CartEntity;

public interface CartService {
  CartEntity getCart(String userId);

  void createDefaultCart(String userId);

  void mergeGuestCartWithUserCart(String userId, String guestId);
}

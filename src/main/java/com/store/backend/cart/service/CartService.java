package com.store.backend.cart.service;

import com.store.backend.cart.entity.CartEntity;

public interface CartService {
  CartEntity createDefaultCart(String userId);
}

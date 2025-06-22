package com.store.backend.cart.service;

import com.store.backend.cart.entity.CartItemEntity;
import com.store.backend.cart.request.AddItemToCartRequest;

public interface CartItemService {
  CartItemEntity addItemToCart(String userId, AddItemToCartRequest request);
}

package com.store.backend.cart.service;

import com.store.backend.cart.entity.CartItemEntity;
import com.store.backend.cart.request.AddItemToCartRequest;
import com.store.backend.cart.request.UpdateItemInCartRequest;

public interface CartItemService {
  CartItemEntity addItemToCart(String userId, AddItemToCartRequest request);

  CartItemEntity updateItemInCart(String id, String userId, UpdateItemInCartRequest request);
}

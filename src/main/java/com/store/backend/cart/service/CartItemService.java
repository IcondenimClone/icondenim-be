package com.store.backend.cart.service;

import com.store.backend.cart.entity.CartEntity;
import com.store.backend.cart.request.AddItemToCartRequest;
import com.store.backend.cart.request.UpdateItemInCartRequest;

public interface CartItemService {
  CartEntity addItemToCart(String userId, AddItemToCartRequest request);

  CartEntity updateItemInCart(String id, String userId, UpdateItemInCartRequest request);

  CartEntity removeItemFromCart(String id, String userId);
}

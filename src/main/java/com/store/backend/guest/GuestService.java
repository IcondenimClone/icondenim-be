package com.store.backend.guest;

import com.store.backend.cart.request.AddItemToCartRequest;
import com.store.backend.cart.request.UpdateItemInCartRequest;
import com.store.backend.guest.request.GuestOrderRequest;
import com.store.backend.guest.response.GuestCartResponse;
import com.store.backend.order.entity.OrderEntity;

public interface GuestService {
  GuestCartResponse guestGetCart(String guestId);

  GuestCartResponse guestAddToCart(String guestId, AddItemToCartRequest request);

  GuestCartResponse guestUpdateInCart(String guestId, String variantId, UpdateItemInCartRequest request);

  GuestCartResponse guestDeleteFromCart(String guestId, String variantId);

  OrderEntity placeGuestOrder(String guestId, GuestOrderRequest request);
}

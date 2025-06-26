package com.store.backend.guest;

import com.store.backend.cart.request.AddItemToCartRequest;
import com.store.backend.cart.request.UpdateItemInCartRequest;
import com.store.backend.guest.response.GuestCartResponse;

public interface GuestService {
  GuestCartResponse guestGetCart(String guestId);

  GuestCartResponse guestAddToCart(String guestId, AddItemToCartRequest request);
  
  GuestCartResponse guestUpdateInCart(String guestId, String variantId, UpdateItemInCartRequest request);

  GuestCartResponse guestDeleteFromCart(String guestId, String variantId);
}

package com.store.backend.guest;

import com.store.backend.cart.request.AddItemToCartRequest;
import com.store.backend.guest.response.GuestCartResponse;

public interface GuestService {
  GuestCartResponse guestAddToCart(String guestId, AddItemToCartRequest request);
}

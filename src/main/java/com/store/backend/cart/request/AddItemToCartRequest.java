package com.store.backend.cart.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddItemToCartRequest {
  private String variantId;
  private int quantity;
}

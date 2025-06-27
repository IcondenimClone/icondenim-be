package com.store.backend.cart.response;

import java.math.BigDecimal;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
  private String id;
  private int totalQuantity;
  private BigDecimal totalPrice;
  private Set<CartItemResponse> items;
}

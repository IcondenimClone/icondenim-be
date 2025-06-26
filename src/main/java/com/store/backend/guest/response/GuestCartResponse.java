package com.store.backend.guest.response;

import java.math.BigDecimal;
import java.util.Set;

import com.store.backend.cart.response.CartItemResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuestCartResponse {
  private int totalQuantity;
  private BigDecimal totalPrice;
  private Set<CartItemResponse> items;
}

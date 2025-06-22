package com.store.backend.cart.response;

import java.math.BigDecimal;
import java.util.Set;

import com.store.backend.user.response.BaseUserResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
  private String id;
  private BaseUserResponse user;
  private int totalQuantity;
  private BigDecimal totalPrice;
  private Set<CartItemResponse> items;
}

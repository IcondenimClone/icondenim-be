package com.store.backend.order.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlaceOrderRequest {
  private String fullName;
  private String phoneNumber;
}

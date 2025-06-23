package com.store.backend.cart.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemInCartRequest {
  @NotNull(message = "Yêu cầu số lượng sản phẩm")
  @Min(value = 1, message = "Số lượng sản phẩm phải hơn hơn 0")
  private int quantity;
}

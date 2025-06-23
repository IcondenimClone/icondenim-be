package com.store.backend.order.response;

import java.math.BigDecimal;

import com.store.backend.variant.response.BaseVariantResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseOrderItemResponse {
  private String id;
  private BaseVariantResponse variant;
  private int quantity;
  private BigDecimal unitPrice;
  private BigDecimal totalPrice;
}

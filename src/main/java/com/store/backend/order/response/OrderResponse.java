package com.store.backend.order.response;

import java.math.BigDecimal;
import java.util.Set;

import com.store.backend.order.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
  private String id;
  private int totalQuantity;
  private BigDecimal totalPrice;
  private BigDecimal discount;
  private BigDecimal totalBill;
  private OrderStatus status;
  private Set<BaseOrderItemResponse> items;
}

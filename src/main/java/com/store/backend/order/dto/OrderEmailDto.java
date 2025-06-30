package com.store.backend.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEmailDto {
  private String to;
  private String subject;
  private String orderId;
  private String confirmLink;
}

package com.store.backend.voucher.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.store.backend.voucher.enums.VoucherType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherResponse {
  private String id;
  private String code;
  private String description;
  private Integer discountPercent;
  private BigDecimal discountAmount;
  private BigDecimal minimumOrderAmount;
  private BigDecimal maximumDiscount;
  private int quantity;
  private int used;
  private int stock;
  private LocalDate startAt;
  private LocalDate endAt;
  private boolean active;
  private VoucherType type;
}

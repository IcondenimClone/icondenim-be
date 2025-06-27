package com.store.backend.voucher.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.store.backend.voucher.enums.VoucherType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateVoucherRequest {
  @NotBlank(message = "Yêu cầu code")
  @Size(max = 50, message = "Code không vượt quá 50 ký tự")
  private String code;

  @NotBlank(message = "Yêu cầu mô tả giảm giá")
  private String description;

  @Min(value = 1, message = "Chiết khấu phải lớn hơn 0%")
  private Integer discountPercent;

  @Min(value = 1000, message = "Giảm giá lơn hơn hoặc bằng 1000₫")
  private BigDecimal discountAmount;

  @Min(value = 1000, message = "Đơn hàng tối thiểu lớn hơn hoặc bằng 1000₫")
  private BigDecimal minimumOrderAmount;

  @Min(value = 1, message = "Số lượng voucher phải lơn hơn 0")
  private Integer quantity;

  @NotNull(message = "Yêu cầu ngày bắt đầu")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate startAt;

  @NotNull(message = "Yêu cầu ngày kết thúc")
  @Future(message = "Thời gian kết thúc giảm giá phải ở tương lại")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate endAt;

  @NotNull(message = "Yêu cầu kiểu voucher")
  @Enumerated(EnumType.STRING)
  private VoucherType type;
}

package com.store.backend.order.request;

import com.store.backend.order.enums.PaymentMethod;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlaceOrderRequest {
  @NotBlank(message = "Yêu cầu tên người nhận")
  @Size(max = 100, message = "Họ tên không quá 100 ký tự")
  private String fullName;

  @NotBlank(message = "Yêu cầu số điện thoại đặt hàng")
  @Size(max = 11, message = "Số điện thoại không quá 11 ký tự")
  private String phoneNumber;

  @NotBlank(message = "Yêu cầu nhập địa chỉ cụ thể")
  private String address;

  @NotBlank(message = "Yêu cầu nhập xã/phường")
  @Size(max = 50, message = "Xã/phường không vượt quá 50 ký tự")
  private String commune;

  @NotBlank(message = "Yêu cầu nhập quận/huyện")
  @Size(max = 50, message = "Quận/huyện không vượt quá 50 ký tự")
  private String district;

  @NotBlank(message = "Yêu cầu nhập tỉnh/thành phố")
  @Size(max = 50, message = "Tỉnh/thành phố không vượt quá 50 ký tự")
  private String province;

  @Enumerated(EnumType.STRING)
  @NotNull(message = "Phương thức thanh toán là bắt buộc")
  private PaymentMethod paymentMethod;
  
  private String note;
}

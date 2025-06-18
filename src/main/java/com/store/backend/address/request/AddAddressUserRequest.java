package com.store.backend.address.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddAddressUserRequest {
  @Size(max = 50, message = "Họ không vượt quá 50 ký tự")
  private String firstName;

  @Size(max = 50, message = "Tên không vượt quá 50 ký tự")
  private String lastName;

  @NotBlank(message = "Yêu cầu số điện thoại")
  @Size(max = 11, message = "Số điện thoại không vượt quá 11 ký tự")
  private String phoneNumber;

  @NotBlank(message = "Yêu càu nhập địa chỉ cụ thể")
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

  private boolean defaultAddress;
}

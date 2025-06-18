package com.store.backend.address.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserAddressRequest {
  @Size(max = 50, message = "Họ không vượt quá 50 ký tự")
  private String firstName;

  @Size(max = 50, message = "Tên không vượt quá 50 ký tự")
  private String lastName;

  @Size(max = 11, message = "Số điện thoại không vượt quá 11 ký tự")
  private String phoneNumber;
  private String address;

  @Size(max = 50, message = "Xã/phường không vượt quá 50 ký tự")
  private String commune;

  @Size(max = 50, message = "Quận/huyện không vượt quá 50 ký tự")
  private String district;

  @Size(max = 50, message = "Tỉnh/thành phố không vượt quá 50 ký tự")
  private String province;
  private boolean defaultAddress;
}

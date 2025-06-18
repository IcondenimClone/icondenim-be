package com.store.backend.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerifySignUpRequest {
  @NotBlank(message = "Yêu cầu mã đăng ký")
  @Size(min = 36, max = 36, message = "Mã đăng ký phải có 36 ký tự")
  @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Yêu gửi lên UUID")
  private String registrationToken;

  @NotBlank(message = "Yêu cầu mã xác thực đăng ký")
  @Size(min = 6, max = 6, message = "Mã xác thực đăng ký phải có 6 ký tự")
  private String otp;
}

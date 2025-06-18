package com.store.backend.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
  @NotBlank(message = "Yêu cầu mã đăng ký")
  @Size(min = 36, max = 36, message = "Mã đăng ký phải có 36 ký tự")
  @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Yêu gửi lên UUID")
  private String resetPasswordToken;

  @NotBlank(message = "Yêu cầu nhập mật khẩu mới")
  @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
  private String newPassword;
}

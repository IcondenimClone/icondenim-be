package com.store.backend.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {
  @NotBlank(message = "Yêu cầu nhập username")
  @Size(min = 3, max = 50, message = "Username có độ dài từ 3 đến 50 ký tự")
  private String username;

  @NotBlank(message = "Yêu cầu nhập mật khẩu")
  @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
  private String password;
}

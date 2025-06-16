package com.store.backend.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {
  @NotBlank(message = "Yêu cầu nhập username")
  @Size(min = 3, max = 50, message = "Username có độ dài từ 3 đến 50 ký tự")
  private String username;

  @NotBlank(message = "Yêu cầu nhập email")
  @Email(message = "Yêu cầu nhập đúng định dạng Email")
  @Size(max = 100, message = "Email không vượt quá 100 kí tự")
  private String email;

  @NotBlank(message = "Yêu cầu nhập mật khẩu")
  @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
  private String password;

  @Size(max = 50, message = "Họ không vượt quá 50 ký tự")
  private String firstName;

  @Size(max = 50, message = "Tên không vượt quá 50 ký tự")
  private String lastName;
}

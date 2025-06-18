package com.store.backend.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {
  @NotBlank(message = "Yêu cầu nhập email")
  @Email(message = "Yêu cầu nhập đúng định dạng Email")
  @Size(max = 100, message = "Email không vượt quá 100 kí tự")
  private String email;
}

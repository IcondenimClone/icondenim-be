package com.store.backend.user.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInfoRequest {
  @Size(max = 50, message = "Họ không vượt quá 50 ký tự")
  private String firstName;

  @Size(max = 50, message = "Tên không vượt quá 50 ký tự")
  private String lastName;
}

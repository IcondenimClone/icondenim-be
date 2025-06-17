package com.store.backend.user.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateInfoRequest {
  @Size(max = 50, message = "Họ không vượt quá 50 ký tự")
  private String firstName;

  @Size(min = 50, message = "Tên không vượt quá 50 ký tự")
  private String lastName;
}

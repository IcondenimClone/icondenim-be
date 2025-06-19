package com.store.backend.size.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSizeRequest {
  @NotBlank(message = "Yêu cầu tên kích cỡ")
  @Size(max = 20, message = "Tên kich cỡ không vượt quá 20 ký tự")
  private String name;
}

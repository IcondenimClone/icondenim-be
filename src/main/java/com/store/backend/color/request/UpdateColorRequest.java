package com.store.backend.color.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateColorRequest {
  @NotBlank(message = "Yêu cầu tên màu sắc")
  @Size(max = 100, message = "Tên màu không vượt quá 100 ký tự")
  private String name;
}

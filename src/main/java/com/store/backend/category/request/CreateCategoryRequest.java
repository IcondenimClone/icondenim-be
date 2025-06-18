package com.store.backend.category.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryRequest {
  @NotBlank(message = "Yêu cầu tên dòng sản phẩm")
  @Size(max = 100, message = "Tên dòng sản phẩm không vượt quá 100 ký tự")
  private String name;

  @Size(min = 36, max = 36, message = "Mã đăng ký phải có 36 ký tự")
  @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Yêu gửi lên UUID")
  private String parentId;
}

package com.store.backend.category.request;

import java.util.Set;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCategoryRequest {
  @Size(max = 100, message = "Tên danh mục sản phẩm không vượt quá 100 ký tự")
  private String name;

  private Set<@Size(min = 36, max = 36, message = "Id danh mục cha phải đúng 36 ký tự") @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Yêu gửi lên UUID") String> parentIds;
}

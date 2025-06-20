package com.store.backend.variant.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVariantRequest {
  @Size(min = 36, max = 36, message = "ID màu sắc phải đúng 36 ký tự")
  @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Yêu gửi lên UUID")
  private String colorId;

  @Size(min = 36, max = 36, message = "ID kích cỡ phải đúng 36 ký tự")
  @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Yêu gửi lên UUID")
  private String sizeId;

  @Size(max = 50, message = "Mã sản phẩm không vượt quá 50 ký tự")
  private String sku;

  @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1")
  private int quantity;
}

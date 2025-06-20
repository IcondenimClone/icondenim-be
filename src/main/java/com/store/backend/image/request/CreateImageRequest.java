package com.store.backend.image.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateImageRequest {
  @NotBlank(message = "Yêu cầu ID sản phẩm")
  @Size(min = 36, max = 36, message = "ID sản phẩm phải đúng 36 ký tự")
  @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Yêu gửi lên UUID")
  private String productId;

  @NotBlank(message = "Yêu cầu ID màu sắc")
  @Size(min = 36, max = 36, message = "ID màu sắc phải đúng 36 ký tự")
  @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Yêu gửi lên UUID")
  private String colorId;

  @NotBlank(message = "Yêu cầu đường dẫn ảnh")
  @Size(max = 255, message = "Đường dẫn ảnh không vượt quá 255 ký tự")
  private String url;

  @Min(value = 0, message = "Thứ tự sắp xếp phải lớn hơn hoặc bằng 0")
  private int sortOrder;
  private boolean thumbnailImage;
}

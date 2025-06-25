package com.store.backend.guest.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestCartItemRequest {
  @NotBlank(message = "Yêu cầu ID thuộc tính sản phẩm")
  @Size(min = 36, max = 36, message = "ID thuộc tính sản phẩm phải đúng 36 ký tự")
  @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Yêu gửi lên UUID")
  private String variantId;

  @NotNull(message = "Yêu cầu số lượng sản phẩm")
  @Min(value = 1, message = "Số lượng sản phẩm phải hơn hơn 0")
  private int quantity;
}

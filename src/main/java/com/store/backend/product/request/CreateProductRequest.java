package com.store.backend.product.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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
public class CreateProductRequest {
  @NotBlank(message = "Yêu cầu tiêu đề sản phẩm")
  private String title;
  private String description;

  @NotNull(message = "Yêu cầu giá sản phẩm")
  @DecimalMin(value = "0.01", message = "Giá phải lớn hơn 0")
  private BigDecimal price;
  private boolean saleProduct;

  @DecimalMin(value = "0.01", message = "Giá phải lớn hơn 0")
  private BigDecimal salePrice;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate startSale;

  @Future(message = "Thời gian kết thúc sale phải là tương lại")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate endSale;

  @NotEmpty
  private Set<@Size(min = 36, max = 36, message = "Id danh mục phải đúng 36 ký tự") @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Yêu gửi lên UUID") String> categoryIds; 
}

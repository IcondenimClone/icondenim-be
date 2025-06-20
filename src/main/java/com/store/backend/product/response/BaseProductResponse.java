package com.store.backend.product.response;

import java.math.BigDecimal;
import java.util.Set;

import com.store.backend.category.response.BaseCategoryResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseProductResponse {
  private String id;
  private String title;
  private String slug;
  private BigDecimal price;
  private boolean saleProduct;
  private BigDecimal salePrice;
  private Set<BaseCategoryResponse> categories;
}

package com.store.backend.product.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.store.backend.category.response.BaseCategoryResponse;
import com.store.backend.image.response.BaseImageResponse;
import com.store.backend.variant.response.BaseVariantResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
  private String id;
  private String title;
  private String slug;
  private BigDecimal price;
  private String description;
  private boolean saleProduct;
  private BigDecimal salePrice;
  private LocalDate startSale;
  private LocalDate endSale;
  private Set<BaseCategoryResponse> categories;
  private Set<BaseVariantResponse> variants;
  private List<BaseImageResponse> images;
}

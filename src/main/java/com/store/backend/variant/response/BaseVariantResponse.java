package com.store.backend.variant.response;

import com.store.backend.color.response.BaseColorResponse;
import com.store.backend.size.response.BaseSizeResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseVariantResponse {
  private String id;
  private String sku;
  private BaseColorResponse color;
  private BaseSizeResponse size;
  private int stock;
  private boolean inStock;
}

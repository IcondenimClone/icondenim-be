package com.store.backend.variant.response;

import com.store.backend.color.response.BaseColorResponse;
import com.store.backend.product.response.BaseProductResponse;
import com.store.backend.size.response.BaseSizeResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VariantResponse {
  private String id;
  private String sku;
  private BaseProductResponse product;
  private BaseColorResponse color;
  private BaseSizeResponse size;
  private int quantity;
  private int quantityPurchase;
  private int stock;
  private boolean inStock;
}

package com.store.backend.image.response;

import com.store.backend.color.response.BaseColorResponse;
import com.store.backend.product.response.BaseProductResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse {
  private String id;
  private BaseProductResponse product;
  private BaseColorResponse color;
  private String url;
  private int sortOrder;
  private boolean thumbnailImage;
}

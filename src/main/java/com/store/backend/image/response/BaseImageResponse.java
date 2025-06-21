package com.store.backend.image.response;

import com.store.backend.color.response.BaseColorResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseImageResponse {
  private String id;
  private BaseColorResponse color;
  private String url;
  private int sortOrder;
  private boolean thumbnailImage;
}

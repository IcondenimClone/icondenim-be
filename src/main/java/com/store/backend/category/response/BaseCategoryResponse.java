package com.store.backend.category.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseCategoryResponse {
  private String id;
  private String name;
  private String slug;
}

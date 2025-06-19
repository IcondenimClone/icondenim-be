package com.store.backend.category.response;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
  private String id;
  private String name;
  private String slug;
  private Set<String> parentIds;
  private Set<BaseCategoryResponse> children;
}

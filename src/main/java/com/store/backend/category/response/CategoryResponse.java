package com.store.backend.category.response;

import java.util.Set;

import com.store.backend.category.dto.ChildCategoryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
  private String id;
  private String name;
  private String slug;
  private Set<String> parentIds;
  private Set<ChildCategoryDto> children;
}

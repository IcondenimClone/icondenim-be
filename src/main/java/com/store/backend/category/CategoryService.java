package com.store.backend.category;

import com.store.backend.category.request.CreateCategoryRequest;

public interface CategoryService {
  CategoryEntity createCategory(CreateCategoryRequest request);

  CategoryEntity getCategoryBySlug(String slug);
}

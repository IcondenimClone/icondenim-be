package com.store.backend.category;

import com.store.backend.category.request.CreateCategoryRequest;
import com.store.backend.category.request.UpdateCategoryRequest;

public interface CategoryService {
  CategoryEntity createCategory(CreateCategoryRequest request);

  CategoryEntity getCategoryBySlug(String slug);

  CategoryEntity updateCategory(String id, UpdateCategoryRequest request);

  void deleteCategory(String id);
}

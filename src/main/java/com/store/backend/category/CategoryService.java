package com.store.backend.category;

import java.util.List;

import com.store.backend.category.request.CreateCategoryRequest;
import com.store.backend.category.request.UpdateCategoryRequest;
import com.store.backend.category.response.CategoryResponse;

public interface CategoryService {
  List<CategoryResponse> getAllCategoryTrees();

  CategoryEntity createCategory(CreateCategoryRequest request);

  CategoryEntity getCategoryBySlug(String slug);

  CategoryEntity updateCategory(String id, UpdateCategoryRequest request);

  void deleteCategory(String id);
}

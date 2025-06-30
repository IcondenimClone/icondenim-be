package com.store.backend.category;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.backend.category.mapper.CategoryMapper;
import com.store.backend.category.request.CreateCategoryRequest;
import com.store.backend.category.request.UpdateCategoryRequest;
import com.store.backend.category.response.CategoryResponse;
import com.store.backend.common.ApiResponse;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
  CategoryService categoryService;
  CategoryMapper categoryMapper;

  @GetMapping("/all/tree")
  public ResponseEntity<ApiResponse> getAllCategoryTrees() {
    List<CategoryResponse> convertedCategories = categoryService.getAllCategoryTrees();
    Map<String, Object> data = Map.of("categories", convertedCategories);
    return ResponseEntity.ok(new ApiResponse("Lấy tất cả danh mục thành công", data));
  }

  @PostMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ApiResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
    CategoryEntity category = categoryService.createCategory(request);
    CategoryResponse convertedCategory = categoryMapper.entityToResponse(category);
    Map<String, Object> data = createCategoryResponse(convertedCategory);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Tạo danh mục sản phẩm thành công", data));
  }

  @GetMapping("/{slug}")
  public ResponseEntity<ApiResponse> getCategoryByName(@PathVariable String slug) {
    CategoryEntity category = categoryService.getCategoryBySlug(slug);
    CategoryResponse convertedCategory = categoryMapper.entityToResponse(category);
    Map<String, Object> data = createCategoryResponse(convertedCategory);
    return ResponseEntity.ok(new ApiResponse("Lấy danh mục sản phẩm thành công", data));
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ApiResponse> updateCategory(@PathVariable String id,
      @Valid @RequestBody UpdateCategoryRequest request) {
    CategoryEntity category = categoryService.updateCategory(id, request);
    CategoryResponse convertedCategory = categoryMapper.entityToResponse(category);
    Map<String, Object> data = createCategoryResponse(convertedCategory);
    return ResponseEntity.ok(new ApiResponse("Cập nhật danh mục sản phẩm thành công", data));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ApiResponse> deleteCategory(@PathVariable String id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.ok(new ApiResponse("Xóa danh mục sản phẩm thành công", null));
  }

  private Map<String, Object> createCategoryResponse(CategoryResponse response) {
    Map<String, Object> data = new HashMap<>();
    data.put("category", response);
    return data;
  }
}

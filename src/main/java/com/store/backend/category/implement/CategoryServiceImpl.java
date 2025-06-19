package com.store.backend.category.implement;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.store.backend.category.CategoryEntity;
import com.store.backend.category.CategoryRepository;
import com.store.backend.category.CategoryService;
import com.store.backend.category.request.CreateCategoryRequest;
import com.store.backend.category.request.UpdateCategoryRequest;
import com.store.backend.common.SlugUtil;
import com.store.backend.exception.AlreadyExistsException;
import com.store.backend.exception.ConflictException;
import com.store.backend.exception.NotFoundException;
import com.store.backend.exception.BadRequestException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
  private final CategoryRepository categoryRepository;

  @Override
  @Transactional
  public CategoryEntity createCategory(CreateCategoryRequest request) {
    String slug = SlugUtil.toSlug(request.getName());
    if (categoryRepository.existsBySlug(slug)) {
      throw new AlreadyExistsException("Danh mục sản phẩm đã tồn tại");
    }

    Set<CategoryEntity> parents = new HashSet<>();
    if (request.getParentIds() != null && !request.getParentIds().isEmpty()) {
      parents = categoryRepository.findAllByIdIn(request.getParentIds());
      if (parents.size() != request.getParentIds().size()) {
        throw new BadRequestException("Có ID danh mục cha không hợp lệ");
      }
    }

    CategoryEntity newCategory = CategoryEntity.builder().name(request.getName()).slug(slug).parents(parents).build();
    validateNoRecursiveLoop(newCategory, parents);
    return categoryRepository.save(newCategory);
  }

  @Override
  public CategoryEntity getCategoryBySlug(String slug) {
    return categoryRepository.findBySlug(slug)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục sản phẩm"));
  }

  @Override
  @Transactional
  public CategoryEntity updateCategory(String id, UpdateCategoryRequest request) {
    CategoryEntity category = categoryRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục sản phẩm"));
    if (request.getName() != null) {
      String slug = SlugUtil.toSlug(request.getName());
      if (categoryRepository.existsBySlug(slug)) {
        throw new AlreadyExistsException("Danh mục sản phẩm đã tồn tại");
      }
      category.setName(request.getName());
      category.setSlug(slug);
    }
    if (request.getParentIds() != null && !request.getParentIds().isEmpty()) {
      if (request.getParentIds().contains(id))
        throw new ConflictException("Không thể thêm Id danh mục này làm danh mục cha");
      Set<CategoryEntity> parents = categoryRepository.findAllByIdIn(request.getParentIds());
      if (parents.size() != request.getParentIds().size()) {
        throw new BadRequestException("Có ID danh mục cha không hợp lệ");
      }
      validateNoRecursiveLoop(category, parents);
      category.setParents(parents);
    }
    return categoryRepository.save(category);
  }

  @Override
  @Transactional
  public void deleteCategory(String id) {
    if (categoryRepository.existsById(id)) {
      categoryRepository.deleteById(id);
    } else {
      throw new NotFoundException("Không tìm thấy danh mục sản phẩm");
    }
  }

  private void validateNoRecursiveLoop(CategoryEntity child, Set<CategoryEntity> newParents) {
    Set<String> visited = new HashSet<>();
    Deque<CategoryEntity> stack = new ArrayDeque<>(newParents);

    while (!stack.isEmpty()) {
      CategoryEntity current = stack.pop();
      if (current.getId().equals(child.getId())) {
        throw new ConflictException("Không thể tạo vòng lặp cha–con");
      }
      if (visited.add(current.getId()) && current.getParents() != null) {
        stack.addAll(current.getParents());
      }
    }
  }
}

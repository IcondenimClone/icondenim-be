package com.store.backend.category.implement;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.store.backend.category.CategoryEntity;
import com.store.backend.category.CategoryRepository;
import com.store.backend.category.CategoryService;
import com.store.backend.category.mapper.CategoryMapper;
import com.store.backend.category.request.CreateCategoryRequest;
import com.store.backend.category.request.UpdateCategoryRequest;
import com.store.backend.category.response.CategoryResponse;
import com.store.backend.common.SlugUtil;
import com.store.backend.exception.AlreadyExistsException;
import com.store.backend.exception.ConflictException;
import com.store.backend.exception.NotFoundException;
import com.store.backend.exception.BadRequestException;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {
  CategoryRepository categoryRepository;
  CategoryMapper categoryMapper;

  @Override
  public List<CategoryResponse> getAllCategoryTrees() {
    List<CategoryEntity> categories = categoryRepository.findAll();
    Map<String, CategoryResponse> idToCategory = new HashMap<>();

    for (CategoryEntity cat : categories) {
      idToCategory.put(cat.getId(), categoryMapper.entityToResponse(cat));
    }

    for (CategoryEntity cat : categories) {
      if (!cat.getParents().isEmpty()) {
        for (CategoryEntity parent : cat.getParents()) {
          CategoryResponse parentRes = idToCategory.get(parent.getId());
          if (parentRes.getChildren() == null) {
            parentRes.setChildren(new HashSet<>());
          }
          parentRes.getChildren().add(categoryMapper.entityToBaseResponse(cat));
        }
      }
    }

    return categories.stream().filter(cat -> cat.getParents().isEmpty()).map(cat -> idToCategory.get(cat.getId()))
        .toList();
  }

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
    if (request.getName() != null && !request.getName().equals(category.getName())) {
      String newSlug = SlugUtil.toSlug(request.getName());
      if (categoryRepository.existsBySlug(newSlug)) {
        throw new AlreadyExistsException("Danh mục sản phẩm đã tồn tại");
      }
      category.setName(request.getName());
      category.setSlug(newSlug);
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
    if (!categoryRepository.existsById(id)) {
      throw new NotFoundException("Không tìm thấy danh mục sản phẩm");
    }
    categoryRepository.deleteById(id);
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

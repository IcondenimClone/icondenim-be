package com.store.backend.category.implement;

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
    CategoryEntity parent = null;
    if (request.getParentId() != null) {
      parent = categoryRepository.findById(request.getParentId())
          .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục cha"));
    }

    String slug = SlugUtil.toSlug(request.getName());
    if (categoryRepository.existsBySlug(slug)) {
      throw new AlreadyExistsException("Slug đã tồn tại");
    }

    CategoryEntity newCategory = CategoryEntity.builder().name(request.getName()).slug(slug).parent(parent).build();
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
    CategoryEntity category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục sản phẩm"));
    if (request.getName() != null) {
      String slug = SlugUtil.toSlug(request.getName());
      if (categoryRepository.existsBySlug(slug)) {
        throw new AlreadyExistsException("Slug đã tồn tại");
      }
      category.setName(request.getName());
      category.setSlug(slug);
    }
    if (request.getParentId() != null) {
      if (request.getParentId().equals(id)) throw new ConflictException("Không thể thêm Id danh mục này làm danh mục cha");
      CategoryEntity parentCategory = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục cha"));
      category.setParent(parentCategory);
    }
    return categoryRepository.save(category);
  }
}

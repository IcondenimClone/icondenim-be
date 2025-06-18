package com.store.backend.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.store.backend.category.CategoryEntity;
import com.store.backend.category.dto.ChildCategoryDto;
import com.store.backend.category.response.CategoryResponse;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
  @Mapping(target = "parentId", source = "parent.id")
  @Mapping(target = "children", source = "children")
  CategoryResponse entityToResponse(CategoryEntity category);

  ChildCategoryDto entityToDto(CategoryEntity category);
}

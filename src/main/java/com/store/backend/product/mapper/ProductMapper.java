package com.store.backend.product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.store.backend.category.CategoryEntity;
import com.store.backend.category.response.BaseCategoryResponse;
import com.store.backend.product.ProductEntity;
import com.store.backend.product.response.ProductResponse;

@Mapper(componentModel = "spring")
public interface ProductMapper {
  @Mapping(target = "categories", source = "categories")
  ProductResponse entityToResponse(ProductEntity product);

  BaseCategoryResponse categoryEntityToBaseCategoryResponse(CategoryEntity category);
}

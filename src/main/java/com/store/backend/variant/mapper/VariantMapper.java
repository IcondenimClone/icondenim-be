package com.store.backend.variant.mapper;

import org.mapstruct.Mapper;

import com.store.backend.variant.VariantEntity;
import com.store.backend.variant.response.VariantResponse;

@Mapper(componentModel = "spring")
public interface VariantMapper {
  VariantResponse entityToResponse(VariantEntity variant);
}

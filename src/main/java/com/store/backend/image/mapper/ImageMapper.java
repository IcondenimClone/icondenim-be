package com.store.backend.image.mapper;

import org.mapstruct.Mapper;

import com.store.backend.image.ImageEntity;
import com.store.backend.image.response.ImageResponse;

@Mapper(componentModel = "spring")
public interface ImageMapper {
  ImageResponse entityToResponse(ImageEntity image);
}

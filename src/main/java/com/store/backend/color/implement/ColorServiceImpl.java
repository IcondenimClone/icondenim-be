package com.store.backend.color.implement;

import org.springframework.stereotype.Service;

import com.store.backend.color.ColorEntity;
import com.store.backend.color.ColorRepository;
import com.store.backend.color.ColorService;
import com.store.backend.color.request.CreateColorRequest;
import com.store.backend.color.request.UpdateColorRequest;
import com.store.backend.color.response.ColorResponse;
import com.store.backend.exception.AlreadyExistsException;
import com.store.backend.exception.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ColorServiceImpl implements ColorService {
  ColorRepository colorRepository;

  @Override
  @Transactional
  public ColorResponse createColor(CreateColorRequest request) {
    if (colorRepository.existsByNameIgnoreCase(request.getName())) {
      throw new AlreadyExistsException("Màu đã tồn tại");
    }
    ColorEntity newColor = ColorEntity.builder().name(request.getName()).build();
    return convertToResponse(colorRepository.save(newColor));
  }

  @Override
  @Transactional
  public ColorResponse updateColor(String id, UpdateColorRequest request) {
    ColorEntity color = colorRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy màu sắc sản phẩm"));
    if (!request.getName().toLowerCase().equals(color.getName().toLowerCase())) {
      if (colorRepository.existsByNameIgnoreCase(request.getName())) {
        throw new AlreadyExistsException("Màu đã tồn tại");
      }
    }
    color.setName(request.getName());
    return convertToResponse(colorRepository.save(color));
  }

  private ColorResponse convertToResponse(ColorEntity color) {
    return ColorResponse.builder().id(color.getId()).name(color.getName()).build();
  }

}

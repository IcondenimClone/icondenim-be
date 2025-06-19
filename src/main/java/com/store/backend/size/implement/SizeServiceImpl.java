package com.store.backend.size.implement;

import org.springframework.stereotype.Service;

import com.store.backend.exception.AlreadyExistsException;
import com.store.backend.exception.NotFoundException;
import com.store.backend.size.SizeEntity;
import com.store.backend.size.SizeRepository;
import com.store.backend.size.SizeService;
import com.store.backend.size.request.CreateSizeRequest;
import com.store.backend.size.request.UpdateSizeRequest;
import com.store.backend.size.response.SizeResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SizeServiceImpl implements SizeService {
  private final SizeRepository sizeRepository;

  @Override
  @Transactional
  public SizeResponse createSize(CreateSizeRequest request) {
    if (sizeRepository.existsByNameIgnoreCase(request.getName())) {
      throw new AlreadyExistsException("Kích cỡ đã tồn tại");
    }
    SizeEntity newSize = SizeEntity.builder().name(request.getName()).build();
    return convertToResponse(sizeRepository.save(newSize));
  }

  @Override
  @Transactional
  public SizeResponse updateSize(String id, UpdateSizeRequest request) {
    SizeEntity size = sizeRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy kích cỡ sản phẩm"));
    if (!request.getName().toLowerCase().equals(size.getName().toLowerCase())) {
      if (sizeRepository.existsByNameIgnoreCase(request.getName())) {
        throw new AlreadyExistsException("Kích cỡ đã tồn tại");
      }
    }
    size.setName(request.getName());
    return convertToResponse(sizeRepository.save(size));
  }

  private SizeResponse convertToResponse(SizeEntity size) {
    return SizeResponse.builder().id(size.getId()).name(size.getName()).build();
  }
}

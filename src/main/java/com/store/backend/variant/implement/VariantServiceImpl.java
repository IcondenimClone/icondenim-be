package com.store.backend.variant.implement;

import org.springframework.stereotype.Service;

import com.store.backend.color.ColorEntity;
import com.store.backend.color.ColorRepository;
import com.store.backend.exception.AlreadyExistsException;
import com.store.backend.exception.BadRequestException;
import com.store.backend.exception.NotFoundException;
import com.store.backend.product.ProductEntity;
import com.store.backend.product.ProductRepository;
import com.store.backend.size.SizeEntity;
import com.store.backend.size.SizeRepository;
import com.store.backend.variant.VariantEntity;
import com.store.backend.variant.VariantRepository;
import com.store.backend.variant.VariantService;
import com.store.backend.variant.request.CreateVariantRequest;
import com.store.backend.variant.request.UpdateVariantRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VariantServiceImpl implements VariantService {
  private final VariantRepository variantRepository;
  private final ProductRepository productRepository;
  private final ColorRepository colorRepository;
  private final SizeRepository sizeRepository;

  @Override
  @Transactional
  public VariantEntity createVariant(CreateVariantRequest request) {
    if (variantRepository.existsBySkuIgnoreCase(request.getSku())) {
      throw new AlreadyExistsException("Mã SKU đã tồn tại");
    }

    ProductEntity product = productRepository.findById(request.getProductId())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));
    ColorEntity color = colorRepository.findById(request.getColorId())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy màu sắc"));
    SizeEntity size = sizeRepository.findById(request.getSizeId())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy kích cỡ"));
    if (variantRepository.existsByProductIdAndColorIdAndSizeId(product.getId(), color.getId(), size.getId())) {
      throw new AlreadyExistsException("Biến thể với màu sắc và kích cỡ này đã tồn tại");
    }

    VariantEntity newVariant = VariantEntity.builder()
        .product(product)
        .color(color)
        .size(size)
        .sku(request.getSku())
        .quantity(request.getQuantity())
        .build();
    newVariant.setStock();
    return variantRepository.save(newVariant);
  }

  @Override
  @Transactional
  public VariantEntity updateVariant(String id, UpdateVariantRequest request) {
    VariantEntity variant = variantRepository.findByIdWithProduct(id)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy biến thể"));
    if (request.getSku() != null && !request.getSku().toLowerCase().equals(variant.getSku().toLowerCase())) {
      if (variantRepository.existsBySkuIgnoreCase(request.getSku())) {
        throw new AlreadyExistsException("Mã SKU đã tồn tại");
      }
      variant.setSku(request.getSku());
    }

    if (request.getColorId() != null) {
      ColorEntity color = colorRepository.findById(request.getColorId()).
          orElseThrow(() -> new NotFoundException("Không tìm thấy màu sắc"));
      if (variantRepository.existsByProductIdAndColorIdAndSizeId(variant.getProduct().getId(), request.getColorId(), variant.getSize().getId())) {
        throw new AlreadyExistsException("Biến thể với màu sắc này đã tồn tại");
      }
      variant.setColor(color);
    }
    if (request.getSizeId() != null) {
      SizeEntity size = sizeRepository.findById(request.getSizeId())
          .orElseThrow(() -> new NotFoundException("Không tìm thấy kích cỡ")); 
      if (variantRepository.existsByProductIdAndColorIdAndSizeId(variant.getProduct().getId(), variant.getColor().getId(), request.getSizeId())) {
        throw new AlreadyExistsException("Biến thể với kích cỡ này đã tồn tại");
      }
      variant.setSize(size);
    }
    if (request.getQuantity() > 0) {
      if (request.getQuantity() < variant.getQuantity()) {
        throw new BadRequestException("Số lượng mới phải lớn hơn hoặc bằng số lượng hiện tại");
      }
      variant.setQuantity(request.getQuantity());
    }
    variant.setStock();
    return variantRepository.save(variant);
  }
}

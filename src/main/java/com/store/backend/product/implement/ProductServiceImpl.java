package com.store.backend.product.implement;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.store.backend.category.CategoryEntity;
import com.store.backend.category.CategoryRepository;
import com.store.backend.common.SlugUtil;
import com.store.backend.exception.AlreadyExistsException;
import com.store.backend.exception.BadRequestException;
import com.store.backend.product.ProductEntity;
import com.store.backend.product.ProductRepository;
import com.store.backend.product.ProductService;
import com.store.backend.product.request.CreateProductRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  @Override
  @Transactional
  public ProductEntity createProduct(CreateProductRequest request) {
    String slug = SlugUtil.toSlug(request.getTitle());
    if (productRepository.existsBySlug(slug)) {
      throw new AlreadyExistsException("Sản phẩm đã tồn tại");
    }

    Set<CategoryEntity> categories = categoryRepository.findAllByIdIn(request.getCategoryIds());
    if (categories.size() != request.getCategoryIds().size()) {
      throw new BadRequestException("Có ID danh mục không hợp lệ");
    }

    if (request.isSaleProduct()) {
      if (request.getSalePrice() == null) {
        throw new BadRequestException("Vui lòng nhập giá khuyến mãi");
      }
      if (request.getSalePrice().compareTo(request.getPrice()) > 0) {
        throw new BadRequestException("Giá khuyến mãi phải nhỏ hơn giá gốc");
      }
      if (request.getStartSale() == null && request.getEndSale() == null) {
        throw new BadRequestException("Vui lòng nhập ngày bắt đầu và kết thúc khuyến mãi");
      }

      LocalDate today = LocalDate.now();
      if (request.getStartSale() != null && request.getStartSale().isBefore(today)) {
        throw new BadRequestException("Thời gian bắt đầu sale không được nhỏ hơn hôm nay");
      }
      if (!request.getEndSale().isAfter(today)) {
        throw new BadRequestException("Ngày kết thúc khuyến mãi phải lớn hơn hôm nay ít nhất 1 ngày");
      }
      if (request.getStartSale() == null && request.getEndSale() != null) {
        request.setStartSale(today);
      }
    } else {
      if (request.getSalePrice() != null) {
        throw new BadRequestException("Không có khuyến mãi nên không thể nhập giá khuyến mãi");
      }
      if (request.getEndSale() != null) {
        throw new BadRequestException("Không có khuyến mãi nên không thể nhập ngày kết thúc khuyến mãi");
      }
      if (request.getStartSale() != null) {
        throw new BadRequestException("Không có khuyến mãi nên không thể nhập ngày bắt đầu khuyến mãi");
      }
    }

    ProductEntity newProduct = ProductEntity.builder().title(request.getTitle()).slug(slug)
        .description(request.getDescription()).price(request.getPrice()).saleProduct(request.isSaleProduct())
        .salePrice(request.getSalePrice()).startSale(request.getStartSale()).endSale(request.getEndSale())
        .categories(categories).build();
    return productRepository.save(newProduct);
  }
}

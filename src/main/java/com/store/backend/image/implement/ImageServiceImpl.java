package com.store.backend.image.implement;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.store.backend.color.ColorEntity;
import com.store.backend.color.ColorRepository;
import com.store.backend.exception.AlreadyExistsException;
import com.store.backend.exception.NotFoundException;
import com.store.backend.image.ImageEntity;
import com.store.backend.image.ImageRepository;
import com.store.backend.image.ImageService;
import com.store.backend.image.request.CreateImageRequest;
import com.store.backend.image.request.UpdateImageRequest;
import com.store.backend.product.ProductEntity;
import com.store.backend.product.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageServiceImpl implements ImageService {
  ImageRepository imageRepository;
  ProductRepository productRepository;
  ColorRepository colorRepository;

  @Override
  @Transactional
  public ImageEntity createImage(CreateImageRequest request) {
    ProductEntity product = productRepository.findById(request.getProductId())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));
    ColorEntity color = colorRepository.findById(request.getColorId())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy màu sắc"));

    if (request.getSortOrder() > 1) {
      if (imageRepository.existsByProductIdAndColorIdAndSortOrder(request.getProductId(), request.getColorId(),
          request.getSortOrder())) {
        throw new AlreadyExistsException("Thứ tự sắp xếp đã tồn tại cho sản phẩm và màu sắc này");
      }
    } else {
      int maxSortOrder = imageRepository.findMaxSortOrderByProductIdAndColorId(request.getProductId(),
          request.getColorId());
      request.setSortOrder(maxSortOrder + 1);
    }

    boolean hasExistingThumbnail = imageRepository
        .existsByProductIdAndColorIdAndThumbnailImage(request.getProductId(), request.getColorId(), true);
    if (request.isThumbnailImage()) {
      if (hasExistingThumbnail) {
        ImageEntity imageThumbnail = imageRepository.findByProductIdAndColorIdAndThumbnailImage(
            request.getProductId(), request.getColorId(), true)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy hình ảnh thumbnail"));
        imageThumbnail.setThumbnailImage(false);
        imageRepository.save(imageThumbnail);
      }
    } else {
      if (!hasExistingThumbnail) {
        request.setThumbnailImage(true);
      }
    }

    ImageEntity image = ImageEntity.builder().product(product).color(color).url(request.getUrl())
        .sortOrder(request.getSortOrder())
        .thumbnailImage(request.isThumbnailImage())
        .build();
    return imageRepository.save(image);
  }

  @Override
  @Transactional
  public ImageEntity updateImage(String id, UpdateImageRequest request) {
    ImageEntity image = imageRepository.findByIdWithProduct(id)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy hình ảnh"));
    String finalProductId = request.getProductId() != null ? request.getProductId() : image.getProduct().getId();
    String finalColorId = request.getColorId() != null ? request.getColorId() : image.getColor().getId();
    if (finalProductId != null) {
      ProductEntity product = productRepository.findById(finalProductId)
          .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));
      image.setProduct(product);
    }

    if (finalColorId != null) {
      ColorEntity color = colorRepository.findById(finalColorId)
          .orElseThrow(() -> new NotFoundException("Không tìm thấy màu sắc"));
      image.setColor(color);
    }

    if (request.getUrl() != null && !request.getUrl().equals(image.getUrl())) {
      image.setUrl(request.getUrl());
    }

    if (request.getSortOrder() > 1) {
      if (imageRepository.existsByProductIdAndColorIdAndSortOrder(finalProductId, finalColorId,
          request.getSortOrder())) {
        throw new AlreadyExistsException("Thứ tự sắp xếp đã tồn tại cho sản phẩm và màu sắc này");
      }
      image.setSortOrder(request.getSortOrder());
    }

    if (request.getThumbnailImage() != null && request.getThumbnailImage() != image.isThumbnailImage()) {
      boolean hasExistingThumbnail = imageRepository
          .existsByProductIdAndColorIdAndThumbnailImageIsTrueAndIdNot(finalProductId, finalColorId, id);
      if (request.getThumbnailImage() == true) {
        if (hasExistingThumbnail) {
          ImageEntity imageThumbnail = imageRepository.findByProductIdAndColorIdAndThumbnailImage(
              finalProductId, finalColorId, true)
              .orElseThrow(() -> new NotFoundException("Không tìm thấy hình ảnh thumbnail"));
          imageThumbnail.setThumbnailImage(false);
          imageRepository.save(imageThumbnail);
        }
        image.setThumbnailImage(true);
      } else {
        if (!hasExistingThumbnail) {
          Optional<ImageEntity> latestImage = imageRepository
              .findTopByProductIdAndColorIdAndIdNotOrderByCreatedAtDesc(
                  finalProductId, finalColorId, id);
          if (latestImage.isPresent()) {
            latestImage.get().setThumbnailImage(true);
            imageRepository.save(latestImage.get());
            image.setThumbnailImage(false);
          } else {
            image.setThumbnailImage(true);
          }
        } else {
          image.setThumbnailImage(false);
        }
      }
    }

    return imageRepository.save(image);
  }
}

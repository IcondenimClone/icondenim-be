package com.store.backend.favorite.implement;

import java.util.List;

import org.springframework.stereotype.Service;

import com.store.backend.exception.NotFoundException;
import com.store.backend.favorite.FavoriteEntity;
import com.store.backend.favorite.FavoriteRepository;
import com.store.backend.favorite.FavoriteService;
import com.store.backend.product.ProductEntity;
import com.store.backend.product.ProductRepository;
import com.store.backend.product.mapper.ProductMapper;
import com.store.backend.product.response.ProductResponse;
import com.store.backend.user.UserEntity;
import com.store.backend.user.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavoriteServiceImpl implements FavoriteService {
  FavoriteRepository favoriteRepository;
  ProductRepository productRepository;
  UserRepository userRepository;
  ProductMapper productMapper;

  @Override
  @Transactional
  public void toggleFavorite(String userId, String productId) {
    if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
      favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    } else {
      ProductEntity product = productRepository.findById(productId)
          .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));
      UserEntity user = userRepository.findById(userId)
          .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
      FavoriteEntity newFavorite = FavoriteEntity.builder().user(user).product(product).build();
      favoriteRepository.save(newFavorite);
    }
  }

  @Override
  @Transactional
  public List<ProductResponse> getUserFavorites(String userId) {
    return favoriteRepository.findAllByUserId(userId).stream().map(FavoriteEntity::getProduct)
        .map(productMapper::entityToResponse).toList();
  }
}

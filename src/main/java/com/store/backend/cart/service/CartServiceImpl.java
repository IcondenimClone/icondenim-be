package com.store.backend.cart.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.store.backend.cart.entity.CartEntity;
import com.store.backend.cart.entity.CartItemEntity;
import com.store.backend.cart.repository.CartRepository;
import com.store.backend.exception.AlreadyExistsException;
import com.store.backend.exception.ForbiddenException;
import com.store.backend.exception.NotFoundException;
import com.store.backend.guest.dto.GuestCartItemDto;
import com.store.backend.redis.RedisService;
import com.store.backend.user.UserEntity;
import com.store.backend.user.UserRepository;
import com.store.backend.user.enums.UserRole;
import com.store.backend.variant.VariantEntity;
import com.store.backend.variant.VariantRepository;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartServiceImpl implements CartService {
  CartRepository cartRepository;
  UserRepository userRepository;
  RedisService redisService;
  VariantRepository variantRepository;

  @Override
  public CartEntity getCart(String userId) {
    return cartRepository.findByUserId(userId)
        .orElseThrow(() -> new NotFoundException("Người dùng chưa có giỏ hàng"));
  }

  @Override
  public void createDefaultCart(String userId) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    if (!user.getRole().equals(UserRole.USER)) {
      throw new ForbiddenException("Không có quyền tạo giỏ hàng");
    }
    if (cartRepository.existsByUserId(userId)) {
      throw new AlreadyExistsException("Người dùng đã có giỏ hàng");
    }
    CartEntity newCart = CartEntity.builder().user(user).build();
    cartRepository.save(newCart);
  }

  @Override
  @Transactional
  public void mergeGuestCartWithUserCart(String userId, String guestId) {
    String redisKey = redisService.setKey(guestId, ":guest:");
    Set<GuestCartItemDto> guestCartItems;
    try {
      guestCartItems = (Set<GuestCartItemDto>) redisService.getObject(redisKey);
    } catch (NotFoundException e) {
      return;
    }
    if (guestCartItems.isEmpty() || guestCartItems == null)
      return;

    CartEntity cart = cartRepository.findByUserId(userId)
        .orElseThrow(() -> new NotFoundException("Người dùng chưa có giỏ hàng"));

    for (GuestCartItemDto guestItem : guestCartItems) {
      Optional<CartItemEntity> existingItem = cart.getItems().stream()
          .filter(item -> item.getVariant().getId().equals(guestItem.getVariantId()))
          .findFirst();

      if (existingItem.isPresent()) {
        existingItem.get().setQuantity(existingItem.get().getQuantity() + guestItem.getQuantity());
        existingItem.get().setUnitPrice();
        existingItem.get().setTotalPrice();
      } else {
        VariantEntity variant = variantRepository.findById(guestItem.getVariantId())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy biến thể sản phẩm"));

        CartItemEntity newItem = CartItemEntity.builder()
            .variant(variant)
            .quantity(guestItem.getQuantity())
            .cart(cart)
            .build();
        newItem.setUnitPrice();
        newItem.setTotalPrice();
        cart.getItems().add(newItem);
      }
    }
    cart.recalculateCart();
    cartRepository.save(cart);
    redisService.deleteObject(redisKey);
  }
}

package com.store.backend.cart.service;

import org.springframework.stereotype.Service;

import com.store.backend.cart.entity.CartEntity;
import com.store.backend.cart.entity.CartItemEntity;
import com.store.backend.cart.repository.CartItemRepository;
import com.store.backend.cart.repository.CartRepository;
import com.store.backend.cart.request.AddItemToCartRequest;
import com.store.backend.exception.NotFoundException;
import com.store.backend.variant.VariantEntity;
import com.store.backend.variant.VariantRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
  private final CartItemRepository cartItemRepository;
  private final CartRepository cartRepository;
  private final VariantRepository variantRepository;

  @Override
  @Transactional
  public CartItemEntity addItemToCart(String userId, AddItemToCartRequest request) {
    CartEntity cart = cartRepository.findByUserId(userId)
        .orElseThrow(() -> new NotFoundException("Người dùng chưa có giỏ hàng"));
    VariantEntity variant = variantRepository.findById(request.getVariantId())
        .orElseThrow(() -> new NotFoundException("Biến thể sản phẩm không tồn tại"));
    CartItemEntity newCartItem = CartItemEntity.builder()
        .cart(cart).variant(variant).quantity(request.getQuantity()).build();
    newCartItem.setUnitPrice();
    newCartItem.setTotalPrice();
    cart.addItem(newCartItem);
    return cartItemRepository.save(newCartItem);
  }
}

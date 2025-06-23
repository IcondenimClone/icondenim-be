package com.store.backend.cart.service;

import org.springframework.stereotype.Service;

import com.store.backend.cart.entity.CartEntity;
import com.store.backend.cart.entity.CartItemEntity;
import com.store.backend.cart.repository.CartItemRepository;
import com.store.backend.cart.repository.CartRepository;
import com.store.backend.cart.request.AddItemToCartRequest;
import com.store.backend.cart.request.UpdateItemInCartRequest;
import com.store.backend.exception.ForbiddenException;
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
    CartItemEntity cartItem = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variant.getId()).orElse(null);
    if (cartItem != null) {
      cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
      cartItem.setUnitPrice();
      cartItem.setTotalPrice();
      cart.recalculateCart();
    } else {
      cartItem = CartItemEntity.builder().cart(cart).variant(variant).quantity(request.getQuantity()).build();
      cartItem.setUnitPrice();
      cartItem.setTotalPrice();
      cart.addItem(cartItem);
    }
    return cartItemRepository.save(cartItem);
  }

  @Override
  @Transactional
  public CartItemEntity updateItemInCart(String id, String userId, UpdateItemInCartRequest request) {
    CartItemEntity cartItem = cartItemRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Mặt hàng trong giỏ hàng không tồn tại"));
    if (!cartItem.getCart().getUser().getId().equals(userId)) {
      throw new ForbiddenException("Bạn không có quyền cập nhật mặt hàng này");
    }
    cartItem.setQuantity(request.getQuantity());
    cartItem.setUnitPrice();
    cartItem.setTotalPrice();

    CartEntity cart = cartItem.getCart();
    cart.recalculateCart();

    return cartItemRepository.save(cartItem);
  }

  @Override
  public void removeItemFromCart(String id, String userId) {
    CartItemEntity cartItem = cartItemRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Mặt hàng trong giỏ hàng không tồn tại"));
    if (!cartItem.getCart().getUser().getId().equals(userId)) {
      throw new ForbiddenException("Bạn không có quyền thực hiện hành động");
    }
    CartEntity cart = cartItem.getCart();
    cart.removeItem(cartItem);
    cartRepository.save(cart);
  }
}

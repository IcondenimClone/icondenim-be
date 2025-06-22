package com.store.backend.cart.service;

import org.springframework.stereotype.Service;

import com.store.backend.cart.entity.CartEntity;
import com.store.backend.cart.repository.CartRepository;
import com.store.backend.exception.AlreadyExistsException;
import com.store.backend.exception.NotFoundException;
import com.store.backend.user.UserEntity;
import com.store.backend.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
  private final CartRepository cartRepository;
  private final UserRepository userRepository;

  @Override
  public CartEntity createDefaultCart(String userId) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    if (cartRepository.existsByUserId(userId)) {
      throw new AlreadyExistsException("Giỏ hàng đã tồn tại");
    }
    CartEntity newCart = CartEntity.builder().user(user).build();
    return cartRepository.save(newCart);
  }
}

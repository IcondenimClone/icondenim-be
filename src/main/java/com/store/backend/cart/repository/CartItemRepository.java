package com.store.backend.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.backend.cart.entity.CartItemEntity;

public interface CartItemRepository extends JpaRepository<CartItemEntity, String> {
  Optional<CartItemEntity> findByCartIdAndVariantId(String cartId, String variantId);
}

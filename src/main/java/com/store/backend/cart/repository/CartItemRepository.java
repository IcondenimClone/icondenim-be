package com.store.backend.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.backend.cart.entity.CartItemEntity;

public interface CartItemRepository extends JpaRepository<CartItemEntity, String> {
}

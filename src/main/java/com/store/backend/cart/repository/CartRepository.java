package com.store.backend.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.backend.cart.entity.CartEntity;

public interface CartRepository extends JpaRepository<CartEntity, String> {
  boolean existsByUserId(String userId);

  Optional<CartEntity> findByUserId(String userId);
}

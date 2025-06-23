package com.store.backend.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.backend.order.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {
  
}

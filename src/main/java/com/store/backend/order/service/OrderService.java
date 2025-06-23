package com.store.backend.order.service;

import com.store.backend.order.entity.OrderEntity;

public interface OrderService {
  OrderEntity placeOrder(String userId);
}

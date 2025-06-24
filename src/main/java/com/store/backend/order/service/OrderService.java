package com.store.backend.order.service;

import com.store.backend.order.entity.OrderEntity;
import com.store.backend.order.request.PlaceOrderRequest;

public interface OrderService {
  OrderEntity placeOrder(String userId, PlaceOrderRequest request);
}

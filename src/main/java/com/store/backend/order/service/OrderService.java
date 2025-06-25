package com.store.backend.order.service;

import com.store.backend.guest.request.GuestOrderRequest;
import com.store.backend.order.entity.OrderEntity;
import com.store.backend.order.request.PlaceOrderRequest;

public interface OrderService {
  OrderEntity placeOrder(String userId, PlaceOrderRequest request);

  OrderEntity guestOrder(GuestOrderRequest request);
}

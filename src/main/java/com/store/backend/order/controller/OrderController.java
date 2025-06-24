package com.store.backend.order.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.backend.common.ApiResponse;
import com.store.backend.order.entity.OrderEntity;
import com.store.backend.order.mapper.OrderMapper;
import com.store.backend.order.request.PlaceOrderRequest;
import com.store.backend.order.response.OrderResponse;
import com.store.backend.order.service.OrderService;
import com.store.backend.user.customs.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
  private final OrderService orderService;
  private final OrderMapper orderMapper;

  @PostMapping
  public ResponseEntity<ApiResponse> placeOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody PlaceOrderRequest request) {
    OrderEntity order = orderService.placeOrder(userDetails.getId(), request);
    OrderResponse convertedOrder = orderMapper.entityToResponse(order);
    Map<String, Object> data = Map.of("order", convertedOrder);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Tạo đơn hàng thành công", data));
  }
}

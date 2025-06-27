package com.store.backend.order.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.store.backend.common.ApiResponse;
import com.store.backend.order.entity.OrderEntity;
import com.store.backend.order.mapper.OrderMapper;
import com.store.backend.order.request.PlaceOrderRequest;
import com.store.backend.order.response.OrderResponse;
import com.store.backend.order.service.OrderService;
import com.store.backend.user.customs.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
  OrderService orderService;
  OrderMapper orderMapper;

  @PostMapping
  public ResponseEntity<ApiResponse> placeOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody PlaceOrderRequest request) {
    OrderEntity order = orderService.placeOrder(userDetails.getId(), request);
    OrderResponse convertedOrder = orderMapper.entityToResponse(order);
    Map<String, Object> data = Map.of("order", convertedOrder);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Tạo đơn hàng thành công", data));
  }

  @GetMapping("/confirm")
  public ResponseEntity<ApiResponse> confirmOrder(@RequestParam String token) {
    orderService.confirmOrder(token);
    return ResponseEntity.ok(new ApiResponse("Xác nhận đơn hàng thành công", null));
  }
}

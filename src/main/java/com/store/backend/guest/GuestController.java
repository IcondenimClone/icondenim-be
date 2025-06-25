package com.store.backend.guest;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.backend.common.ApiResponse;
import com.store.backend.guest.request.GuestOrderRequest;
import com.store.backend.order.entity.OrderEntity;
import com.store.backend.order.mapper.OrderMapper;
import com.store.backend.order.response.OrderResponse;
import com.store.backend.order.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/guest")
public class GuestController {
  private final OrderService orderService;
  private final OrderMapper orderMapper;

  @PostMapping("/orders")
  public ResponseEntity<ApiResponse> guestOrder(@Valid @RequestBody GuestOrderRequest request) {
    OrderEntity order = orderService.guestOrder(request);
    OrderResponse convertedOrder = orderMapper.entityToResponse(order);
    Map<String, Object> data = Map.of("order", convertedOrder);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Tạo đơn hàng thành công", data));
  }
}

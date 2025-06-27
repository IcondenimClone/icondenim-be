package com.store.backend.cart.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.backend.cart.entity.CartEntity;
import com.store.backend.cart.mapper.CartMapper;
import com.store.backend.cart.response.CartResponse;
import com.store.backend.cart.service.CartService;
import com.store.backend.common.ApiResponse;
import com.store.backend.user.customs.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {
  private final CartService cartService;
  private final CartMapper cartMapper;

  @GetMapping
  public ResponseEntity<ApiResponse> getCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
    CartEntity cart = cartService.getCart(userDetails.getId());
    CartResponse convertedCart = cartMapper.entityToResponse(cart);
    Map<String, Object> data = Map.of("cart", convertedCart);
    return ResponseEntity.ok(new ApiResponse("Lấy giỏ hàng thành công", data));
  }

  // @PostMapping("/default")
  // public ResponseEntity<ApiResponse> createDefaultCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
  //   CartEntity cart = cartService.createDefaultCart(userDetails.getId());
  //   CartResponse convertedCart = cartMapper.entityToResponse(cart);
  //   Map<String, Object> data = Map.of("cart", convertedCart);
  //   return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Tạo giỏ hàng mặc định thành công", data));
  // }
}

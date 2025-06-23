package com.store.backend.cart.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.backend.cart.entity.CartItemEntity;
import com.store.backend.cart.mapper.CartItemMapper;
import com.store.backend.cart.request.AddItemToCartRequest;
import com.store.backend.cart.request.UpdateItemInCartRequest;
import com.store.backend.cart.response.CartItemResponse;
import com.store.backend.cart.service.CartItemService;
import com.store.backend.common.ApiResponse;
import com.store.backend.user.customs.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart-items")
public class CartItemController {
  private final CartItemService cartItemService;
  private final CartItemMapper cartItemMapper;

  @PostMapping
  public ResponseEntity<ApiResponse> addItemToCart(@AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody AddItemToCartRequest request) {
    CartItemEntity cartItem = cartItemService.addItemToCart(userDetails.getId(), request);
    CartItemResponse convertedCartItem = cartItemMapper.entityToResponse(cartItem);
    Map<String, Object> data = Map.of("cartItem", convertedCartItem);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ApiResponse("Thêm mặt hàng vào giỏ hàng thành công", data));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse> updateItemInCart(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String id,
      @Valid @RequestBody UpdateItemInCartRequest request) {
    CartItemEntity cart = cartItemService.updateItemInCart(id, userDetails.getId(), request);
    CartItemResponse convertedCartItem = cartItemMapper.entityToResponse(cart);
    Map<String, Object> data = Map.of("cartItem", convertedCartItem);
    return ResponseEntity.ok(new ApiResponse("Cập nhật mặt hàng trong giỏ hàng thành công", data));
  }
}

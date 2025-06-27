package com.store.backend.cart.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.backend.cart.entity.CartEntity;
import com.store.backend.cart.mapper.CartMapper;
import com.store.backend.cart.request.AddItemToCartRequest;
import com.store.backend.cart.request.UpdateItemInCartRequest;
import com.store.backend.cart.response.CartResponse;
import com.store.backend.cart.service.CartItemService;
import com.store.backend.common.ApiResponse;
import com.store.backend.user.customs.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart-items")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemController {
  CartItemService cartItemService;
  CartMapper cartMapper;

  @PostMapping
  public ResponseEntity<ApiResponse> addItemToCart(@AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody AddItemToCartRequest request) {
    CartEntity cart = cartItemService.addItemToCart(userDetails.getId(), request);
    CartResponse convertedCart = cartMapper.entityToResponse(cart);
    Map<String, Object> data = Map.of("cart", convertedCart);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ApiResponse("Thêm mặt hàng vào giỏ hàng thành công", data));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse> updateItemInCart(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String id,
      @Valid @RequestBody UpdateItemInCartRequest request) {
    CartEntity cart = cartItemService.updateItemInCart(id, userDetails.getId(), request);
    CartResponse convertedCart = cartMapper.entityToResponse(cart);
    Map<String, Object> data = Map.of("cart", convertedCart);
    return ResponseEntity.ok(new ApiResponse("Cập nhật mặt hàng trong giỏ hàng thành công", data));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse> removeItemFromCart(@AuthenticationPrincipal CustomUserDetails userDetails,  @PathVariable String id) {
    CartEntity cart = cartItemService.removeItemFromCart(id, userDetails.getId());
    CartResponse convertedCart = cartMapper.entityToResponse(cart);
    Map<String, Object> data = Map.of("cart", convertedCart);
    return ResponseEntity.ok(new ApiResponse("Xóa mặt hàng khỏi giỏ hàng thành công", data));
  }
}

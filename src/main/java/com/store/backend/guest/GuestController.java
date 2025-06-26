package com.store.backend.guest;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.backend.cart.request.AddItemToCartRequest;
import com.store.backend.cart.request.UpdateItemInCartRequest;
import com.store.backend.common.ApiResponse;
import com.store.backend.guest.request.GuestOrderRequest;
import com.store.backend.guest.response.GuestCartResponse;
import com.store.backend.order.entity.OrderEntity;
import com.store.backend.order.mapper.OrderMapper;
import com.store.backend.order.response.OrderResponse;
import com.store.backend.order.service.OrderService;
import com.store.backend.security.JwtService;
import com.store.backend.user.customs.CustomUserDetails;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/guest")
public class GuestController {
  @Value("${jwt.guest-token-name}")
  private String guestTokenName;

  @Value("${server.servlet.context-path}")
  private String apiPrefix;

  private final OrderService orderService;
  private final GuestService guestService;
  private final OrderMapper orderMapper;
  private final JwtService jwtService;

  @PostMapping("/cart-items")
  public ResponseEntity<ApiResponse> guestAddToCart(@AuthenticationPrincipal CustomUserDetails userDetails,
      HttpServletRequest request, @Valid @RequestBody AddItemToCartRequest cliRequest, HttpServletResponse response) {
    if (userDetails != null) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse("Bạn không có quyền truy cập", null));
    }
    String token = extractGuestTokenFromCookie(request);
    String guestId;
    if (token == null) {
      guestId = UUID.randomUUID().toString();
    } else {
      guestId = jwtService.extractGuestId(token);
    }
    GuestCartResponse cart = guestService.guestAddToCart(guestId, cliRequest);
    String guestToken = jwtService.generateGuestToken(guestId);
    setTokenCookie(response, guestTokenName, guestToken, apiPrefix + "/guest", 30 * 24 * 60 * 60);
    Map<String, Object> data = Map.of("cart", cart);
    return ResponseEntity.ok(new ApiResponse("Thêm vào giỏ hàng thành công", data));
  }

  @PutMapping("/cart-items/items/{variantId}")
  public ResponseEntity<ApiResponse> guestUpdateInCart(@AuthenticationPrincipal CustomUserDetails userDetails,
      HttpServletRequest request, @PathVariable String variantId,
      @Valid @RequestBody UpdateItemInCartRequest cliRequest, HttpServletResponse response) {
    if (userDetails != null) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse("Bạn không có quyền truy cập", null));
    }
    String token = extractGuestTokenFromCookie(request);
    if (token == null) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse("Bạn không có quyền truy cập", null));
    }
    String guestId = jwtService.extractGuestId(token);
    GuestCartResponse cart = guestService.guestUpdateInCart(guestId, variantId, cliRequest);
    String guestToken = jwtService.generateGuestToken(guestId);
    setTokenCookie(response, guestTokenName, guestToken, apiPrefix + "/guest", 30 * 24 * 60 * 60);
    Map<String, Object> data = Map.of("cart", cart);
    return ResponseEntity.ok(new ApiResponse("Cập nhật giỏ hàng thành công", data));
  }

  @PostMapping("/orders")
  public ResponseEntity<ApiResponse> guestOrder(@Valid @RequestBody GuestOrderRequest request) {
    OrderEntity order = orderService.guestOrder(request);
    OrderResponse convertedOrder = orderMapper.entityToResponse(order);
    Map<String, Object> data = Map.of("order", convertedOrder);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Tạo đơn hàng thành công", data));
  }

  private void setTokenCookie(HttpServletResponse response, String name, String value, String path, int maxAge) {
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(true);
    cookie.setSecure(false);
    cookie.setPath(path);
    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);
  }

  private String extractGuestTokenFromCookie(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (guestTokenName.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
}

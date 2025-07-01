package com.store.backend.favorite;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.backend.common.ApiResponse;
import com.store.backend.product.response.ProductResponse;
import com.store.backend.user.customs.CustomUserDetails;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavoriteController {
  FavoriteService favoriteService;

  @PostMapping("/products/{productId}")
  public ResponseEntity<ApiResponse> toggleFavorite(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String productId) {
    favoriteService.toggleFavorite(userDetails.getId(), productId);
    return ResponseEntity.ok(new ApiResponse("Cập nhật yêu thích thành công", null));
  }

  @GetMapping("/my")
  public ResponseEntity<ApiResponse> getUserFavorites(@AuthenticationPrincipal CustomUserDetails userDetails) {
    List<ProductResponse> convertedProducts = favoriteService.getUserFavorites(userDetails.getId());
    Map<String, Object> data = Map.of("favorites", convertedProducts);
    return ResponseEntity.ok(new ApiResponse("Lấy danh sách sản phẩm yêu thích thành công", data));
  }
}

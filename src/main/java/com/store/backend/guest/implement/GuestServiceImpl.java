package com.store.backend.guest.implement;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.store.backend.cart.request.AddItemToCartRequest;
import com.store.backend.cart.response.CartItemResponse;
import com.store.backend.color.response.BaseColorResponse;
import com.store.backend.exception.NotFoundException;
import com.store.backend.guest.GuestService;
import com.store.backend.guest.dto.GuestCartItemDto;
import com.store.backend.guest.response.GuestCartResponse;
import com.store.backend.product.ProductEntity;
import com.store.backend.redis.RedisService;
import com.store.backend.size.response.BaseSizeResponse;
import com.store.backend.variant.VariantEntity;
import com.store.backend.variant.VariantRepository;
import com.store.backend.variant.response.BaseVariantResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService {
  @Value("${jwt.guest-token-expiration:30}")
  private long guestTokenExpirationDays;

  private final RedisService redisService;
  private final VariantRepository variantRepository;

  @Override
  @Transactional
  public GuestCartResponse guestAddToCart(String guestId, AddItemToCartRequest request) {
    String redisKey = redisService.setKey(guestId, ":guest:");
    Set<GuestCartItemDto> cartItems;
    try {
      cartItems = (Set<GuestCartItemDto>) redisService.getObject(redisKey);
    } catch (NotFoundException e) {
      System.out.println("Không có khóa");
      cartItems = new HashSet<>();
    }

    System.out.println("Giỏ hàng ban đầu: " + cartItems);

    GuestCartItemDto newItem = GuestCartItemDto.builder().variantId(request.getVariantId())
        .quantity(request.getQuantity()).build();

    Optional<GuestCartItemDto> existingItem = cartItems.stream()
        .filter(item -> item.getVariantId().equals(newItem.getVariantId()))
        .findFirst();
    if (existingItem.isPresent()) {
      existingItem.get().setQuantity(existingItem.get().getQuantity() + newItem.getQuantity());
    } else {
      cartItems.add(newItem);
    }

    System.out.println("Giỏ hàng trước khi lưu vào redis" + cartItems);

    redisService.saveObject(redisKey, cartItems, guestTokenExpirationDays, TimeUnit.DAYS);
    return buildGuestCartResponse(cartItems);
  }

  private GuestCartResponse buildGuestCartResponse(Set<GuestCartItemDto> cartItems) {
    Set<CartItemResponse> itemResponses = new HashSet<>();
    int totalQuantity = 0;
    BigDecimal totalPrice = BigDecimal.ZERO;

    for (GuestCartItemDto dto : cartItems) {
      VariantEntity variant = variantRepository.findById(dto.getVariantId())
          .orElseThrow(() -> new NotFoundException("Không tìm thấy biến thể"));

      ProductEntity product = variant.getProduct();
      BigDecimal unitPrice = product.isSaleProduct() ? product.getSalePrice() : product.getPrice();
      BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(dto.getQuantity()));

      totalQuantity += dto.getQuantity();
      totalPrice = totalPrice.add(itemTotal);

      BaseVariantResponse variantResponse = BaseVariantResponse.builder()
          .id(variant.getId())
          .sku(variant.getSku())
          .color(BaseColorResponse.builder()
              .id(variant.getColor().getId())
              .name(variant.getColor().getName())
              .build())
          .size(BaseSizeResponse.builder()
              .id(variant.getSize().getId())
              .name(variant.getSize().getName())
              .build())
          .stock(variant.getStock())
          .inStock(variant.isInStock())
          .build();

      CartItemResponse itemResponse = CartItemResponse.builder()
          .variant(variantResponse)
          .quantity(dto.getQuantity())
          .unitPrice(unitPrice)
          .totalPrice(itemTotal)
          .build();

      itemResponses.add(itemResponse);
    }

    return GuestCartResponse.builder()
        .totalQuantity(totalQuantity)
        .totalPrice(totalPrice)
        .items(itemResponses)
        .build();
  }
}

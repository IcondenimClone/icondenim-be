package com.store.backend.guest.implement;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.store.backend.cart.request.AddItemToCartRequest;
import com.store.backend.cart.request.UpdateItemInCartRequest;
import com.store.backend.cart.response.BaseCartItemResponse;
import com.store.backend.color.response.BaseColorResponse;
import com.store.backend.exception.ConflictException;
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
  public GuestCartResponse guestGetCart(String guestId) {
    String redisKey = redisService.setKey(guestId, ":guest:");
    Set<GuestCartItemDto> cartItems;
    try {
      cartItems = (Set<GuestCartItemDto>) redisService.getObject(redisKey);
    } catch (NotFoundException e) {
      cartItems = new HashSet<>();
    }
    return buildGuestCartResponse(cartItems);
  }

  @Override
  @Transactional
  public GuestCartResponse guestAddToCart(String guestId, AddItemToCartRequest request) {
    String redisKey = redisService.setKey(guestId, ":guest:");
    Set<GuestCartItemDto> cartItems;
    try {
      cartItems = (Set<GuestCartItemDto>) redisService.getObject(redisKey);
    } catch (NotFoundException e) {
      cartItems = new HashSet<>();
    }

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

    redisService.saveObject(redisKey, cartItems, guestTokenExpirationDays, TimeUnit.DAYS);
    return buildGuestCartResponse(cartItems);
  }

  @Override
  @Transactional
  public GuestCartResponse guestUpdateInCart(String guestId, String variantId, UpdateItemInCartRequest request) {
    String redisKey = redisService.setKey(guestId, ":guest:");
    Set<GuestCartItemDto> cartItems;
    try {
      cartItems = (Set<GuestCartItemDto>) redisService.getObject(redisKey);
    } catch (NotFoundException e) {
      throw new ConflictException("Chưa có giỏ hàng");
    }
    if (cartItems == null || cartItems.isEmpty()) {
      throw new ConflictException("Giỏ hàng trống");
    }

    Optional<GuestCartItemDto> existingItem = cartItems.stream()
        .filter(item -> item.getVariantId().equals(variantId))
        .findFirst();
    if (existingItem.isPresent()) {
      existingItem.get().setQuantity(request.getQuantity());
    } else {
      throw new NotFoundException("Không tìm thấy sản phẩm trong giỏ hàng");
    }

    redisService.saveObject(redisKey, cartItems, guestTokenExpirationDays, TimeUnit.DAYS);
    return buildGuestCartResponse(cartItems);
  }

  @Override
  @Transactional
  public GuestCartResponse guestDeleteFromCart(String guestId, String variantId) {
    String redisKey = redisService.setKey(guestId, ":guest:");
    Set<GuestCartItemDto> cartItems;
    try {
      cartItems = (Set<GuestCartItemDto>) redisService.getObject(redisKey);
    } catch (NotFoundException e) {
      throw new ConflictException("Chưa có giỏ hàng");
    }
    if (cartItems == null || cartItems.isEmpty()) {
      throw new ConflictException("Giỏ hàng trống");
    }

    boolean removed = cartItems.removeIf(item -> item.getVariantId().equals(variantId));
    if (!removed) {
      throw new NotFoundException("Không tìm thấy sản phẩm trong giỏ hàng");
    }
    if (cartItems.isEmpty()) {
      redisService.deleteObject(redisKey);
    } else {
      redisService.saveObject(redisKey, cartItems, guestTokenExpirationDays, TimeUnit.DAYS);
    }
    return buildGuestCartResponse(cartItems);
  }

  private GuestCartResponse buildGuestCartResponse(Set<GuestCartItemDto> cartItems) {
    Set<BaseCartItemResponse> itemResponses = new HashSet<>();
    int totalQuantity = 0;
    BigDecimal totalPrice = BigDecimal.ZERO;

    for (GuestCartItemDto dto : cartItems) {
      VariantEntity variant = variantRepository.findById(dto.getVariantId())
          .orElseThrow(() -> new NotFoundException("Không tìm thấy biến thể sản phẩm"));

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

      BaseCartItemResponse itemResponse = BaseCartItemResponse.builder()
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

package com.store.backend.cart.mapper;

import org.mapstruct.Mapper;

import com.store.backend.cart.entity.CartItemEntity;
import com.store.backend.cart.response.CartItemResponse;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
  CartItemResponse entityToResponse(CartItemEntity cartItem);
}

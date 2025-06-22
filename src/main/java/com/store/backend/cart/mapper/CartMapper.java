package com.store.backend.cart.mapper;

import org.mapstruct.Mapper;

import com.store.backend.cart.entity.CartEntity;
import com.store.backend.cart.response.CartResponse;

@Mapper(componentModel = "spring")
public interface CartMapper {
  CartResponse entityToResponse(CartEntity cart);
}

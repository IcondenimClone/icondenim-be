package com.store.backend.order.mapper;

import org.mapstruct.Mapper;

import com.store.backend.order.entity.OrderEntity;
import com.store.backend.order.response.OrderResponse;

@Mapper(componentModel = "spring")
public interface OrderMapper {
  OrderResponse entityToResponse(OrderEntity order);
}

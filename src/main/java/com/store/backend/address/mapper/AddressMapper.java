package com.store.backend.address.mapper;

import org.mapstruct.Mapper;

import com.store.backend.address.AddressEntity;
import com.store.backend.address.response.AddressResponse;

@Mapper(componentModel = "spring")
public interface AddressMapper {
  AddressResponse entityToResponse(AddressEntity address);
}

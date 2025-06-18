package com.store.backend.address.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.store.backend.address.AddressEntity;
import com.store.backend.address.request.UpdateUserAddressRequest;
import com.store.backend.address.response.AddressResponse;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressMapper {
  AddressResponse entityToResponse(AddressEntity address);

  List<AddressResponse> listEntitiesToListResponses(List<AddressEntity> addresses);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  void patchRequestToEntity(UpdateUserAddressRequest request, @MappingTarget AddressEntity address);
}

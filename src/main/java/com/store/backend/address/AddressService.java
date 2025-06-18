package com.store.backend.address;

import java.util.List;

import com.store.backend.address.request.AddAddressUserRequest;
import com.store.backend.address.request.UpdateUserAddressRequest;

public interface AddressService {
  AddressEntity addUserAddress(AddAddressUserRequest request, String userId);

  AddressEntity getUserAddressById(String id, String userId);

  List<AddressEntity> getUserAddresses(String userId);

  AddressEntity updateUserAddress(String id, UpdateUserAddressRequest request, String userId);

  void deleteUserAddress(String id, String userId);
}

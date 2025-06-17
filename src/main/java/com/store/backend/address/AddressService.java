package com.store.backend.address;

import com.store.backend.address.request.AddAddressRequest;

public interface AddressService {
  AddressEntity addAddress(AddAddressRequest request, String userId);
}

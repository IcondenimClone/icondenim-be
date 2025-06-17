package com.store.backend.address.implement;

import java.util.List;

import org.springframework.stereotype.Service;

import com.store.backend.address.AddressEntity;
import com.store.backend.address.AddressRepository;
import com.store.backend.address.AddressService;
import com.store.backend.address.request.AddAddressRequest;
import com.store.backend.exception.NotFoundException;
import com.store.backend.user.UserEntity;
import com.store.backend.user.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {
  private final AddressRepository addressRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public AddressEntity addAddress(AddAddressRequest request, String userId) {
    log.info("Request body: {}", request);

    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    if (request.isDefaultAddress()) {
      List<AddressEntity> currentAddresses = addressRepository.findByUserId(userId);
      for (AddressEntity address : currentAddresses) {
        if (address.isDefaultAddress()) {
          address.setDefaultAddress(false);
        }
      }
      addressRepository.saveAll(currentAddresses);
    }

    AddressEntity newAddress = AddressEntity.builder().firstName(request.getFirstName()).lastName(request.getLastName())
        .phoneNumber(request.getPhoneNumber()).address(request.getAddress()).commune(request.getCommune())
        .district(request.getDistrict()).province(request.getProvince()).defaultAddress(request.isDefaultAddress()).user(user)
        .build();
    return addressRepository.save(newAddress);
  }
}

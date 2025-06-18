package com.store.backend.address.implement;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.store.backend.address.AddressEntity;
import com.store.backend.address.AddressRepository;
import com.store.backend.address.AddressService;
import com.store.backend.address.mapper.AddressMapper;
import com.store.backend.address.request.AddAddressUserRequest;
import com.store.backend.address.request.UpdateUserAddressRequest;
import com.store.backend.exception.ForbiddenException;
import com.store.backend.exception.NotFoundException;
import com.store.backend.exception.TooManyException;
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
  private final AddressMapper addressMapper;

  @Override
  @Transactional
  public AddressEntity addUserAddress(AddAddressUserRequest request, String userId) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));

    long addressNumber = addressRepository.countByUserId(userId);
    if (addressNumber >= 10) {
      throw new TooManyException("Số lượng địa chỉ của bạn đã tối đa");
    }
    if (addressNumber == 0) {
      request.setDefaultAddress(true);
    }
    if (request.isDefaultAddress() && addressNumber > 0) {
      List<AddressEntity> currentAddresses = addressRepository.findAllByUserId(userId);
      for (AddressEntity address : currentAddresses) {
        if (address.isDefaultAddress()) {
          address.setDefaultAddress(false);
        }
      }
      addressRepository.saveAll(currentAddresses);
    }

    AddressEntity newAddress = AddressEntity.builder().firstName(request.getFirstName()).lastName(request.getLastName())
        .phoneNumber(request.getPhoneNumber()).address(request.getAddress()).commune(request.getCommune())
        .district(request.getDistrict()).province(request.getProvince()).defaultAddress(request.isDefaultAddress())
        .user(user)
        .build();
    return addressRepository.save(newAddress);
  }

  @Override
  public AddressEntity getUserAddressById(String id, String userId) {
    AddressEntity address = addressRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy địa chỉ"));
    if (!address.getUser().getId().equals(userId)) {
      throw new ForbiddenException("Bạn không có quyền truy cập địa chỉ này");
    }
    return address;
  }

  @Override
  public List<AddressEntity> getUserAddresses(String userId) {
    return addressRepository.findAllByUserId(userId);
  }

  @Override
  @Transactional
  public AddressEntity updateUserAddress(String id, UpdateUserAddressRequest request, String userId) {
    AddressEntity address = addressRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy địa chỉ"));
    if (!address.getUser().getId().equals(userId)) {
      throw new ForbiddenException("Bạn không có quyền truy cập địa chỉ này");
    }

    long addressNumber = addressRepository.countByUserId(userId);
    if (addressNumber == 1 && !request.isDefaultAddress()) {
      request.setDefaultAddress(true);
    }

    if (request.isDefaultAddress()) {
      List<AddressEntity> currentAddresses = addressRepository.findAllByUserId(userId);
      for (AddressEntity other : currentAddresses) {
        if (!other.getId().equals(id) && other.isDefaultAddress()) {
          other.setDefaultAddress(false);
        }
      }
      addressRepository.saveAll(currentAddresses);
    } else {
      List<AddressEntity> otherAddresses = addressRepository.findAllByUserId(userId).stream()
          .filter(a -> !a.getId().equals(id)).sorted(Comparator.comparing(AddressEntity::getCreatedAt).reversed())
          .toList();
      if (!otherAddresses.isEmpty()) {
        AddressEntity newest = otherAddresses.get(0);
        newest.setDefaultAddress(true);
        addressRepository.save(newest);
      } else {
        request.setDefaultAddress(true);
      }
    }

    addressMapper.patchRequestToEntity(request, address);
    return addressRepository.save(address);
  }

  @Override
  @Transactional
  public void deleteUserAddress(String id, String userId) {
    AddressEntity address = addressRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy địa chỉ"));
    if (!address.getUser().getId().equals(userId)) {
      throw new ForbiddenException("Bạn không có quyền truy cập địa chỉ này");
    }
    addressRepository.delete(address);
  }
}

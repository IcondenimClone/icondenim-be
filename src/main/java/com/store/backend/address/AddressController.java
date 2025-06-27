package com.store.backend.address;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.backend.address.mapper.AddressMapper;
import com.store.backend.address.request.AddAddressUserRequest;
import com.store.backend.address.request.UpdateUserAddressRequest;
import com.store.backend.address.response.AddressResponse;
import com.store.backend.common.ApiResponse;
import com.store.backend.user.customs.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/addresses")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressController {
  AddressService addressService;
  AddressMapper addressMapper;

  @PostMapping("/my")
  public ResponseEntity<ApiResponse> addUserAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody AddAddressUserRequest request) {
    AddressEntity address = addressService.addUserAddress(request, userDetails.getId());
    AddressResponse convertedAddress = addressMapper.entityToResponse(address);
    Map<String, Object> data = createAddressResponse(convertedAddress);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Thêm địa chỉ thành công", data));
  }

  @GetMapping("/my/{id}")
  public ResponseEntity<ApiResponse> getUserAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable String id) {
    AddressEntity address = addressService.getUserAddressById(id, userDetails.getId());
    AddressResponse convertedAddress = addressMapper.entityToResponse(address);
    Map<String, Object> data = createAddressResponse(convertedAddress);
    return ResponseEntity.ok(new ApiResponse("Lấy địa chỉ thành công", data));
  }

  @GetMapping("/my")
  public ResponseEntity<ApiResponse> getUserAddresses(@AuthenticationPrincipal CustomUserDetails userDetails) {
    List<AddressEntity> addresses = addressService.getUserAddresses(userDetails.getId());
    List<AddressResponse> convertedAddresses = addressMapper.listEntitiesToListResponses(addresses);
    Map<String, Object> data = new HashMap<>();
    data.put("addresses", convertedAddresses);
    return ResponseEntity.ok(new ApiResponse("Lấy tất cả địa chỉ của người dùng thành công", data));
  }

  @PatchMapping("/my/{id}")
  public ResponseEntity<ApiResponse> updateUserAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable String id, @Valid @RequestBody UpdateUserAddressRequest request) {
    AddressEntity address = addressService.updateUserAddress(id, request, userDetails.getId());
    AddressResponse convertedAddress = addressMapper.entityToResponse(address);
    Map<String, Object> data = createAddressResponse(convertedAddress);
    return ResponseEntity.ok(new ApiResponse("Cập nhật địa chỉ thành công", data));
  }

  @DeleteMapping("/my/{id}")
  public ResponseEntity<ApiResponse> deleteUserAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable String id) {
    addressService.deleteUserAddress(id, userDetails.getId());
    return ResponseEntity.ok(new ApiResponse("Xóa địa chỉ thành công", null));
  }

  private Map<String, Object> createAddressResponse(AddressResponse addressResponse) {
    Map<String, Object> data = new HashMap<>();
    data.put("address", addressResponse);
    return data;
  }
}

package com.store.backend.address;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.backend.address.mapper.AddressMapper;
import com.store.backend.address.request.AddAddressRequest;
import com.store.backend.address.response.AddressResponse;
import com.store.backend.common.ApiResponse;
import com.store.backend.user.customs.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/addresses")
public class AddressController {
  private final AddressService addressService;
  private final AddressMapper addressMapper;

  @PostMapping()
  public ResponseEntity<ApiResponse> addAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody AddAddressRequest request) {
    AddressEntity address = addressService.addAddress(request, userDetails.getId());
    AddressResponse convertedAddress = addressMapper.entityToResponse(address);
    Map<String, Object> data = new HashMap<>();
    data.put("address", convertedAddress);
    return ResponseEntity.ok(new ApiResponse("Thêm địa chỉ thành công", data));
  }

}

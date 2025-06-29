package com.store.backend.voucher;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.backend.common.ApiResponse;
import com.store.backend.voucher.mapper.VoucherMapper;
import com.store.backend.voucher.request.CreateVoucherRequest;
import com.store.backend.voucher.response.VoucherResponse;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vouchers")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherController {
  VoucherService voucherService;
  VoucherMapper voucherMapper;

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<ApiResponse> createVoucher(@Valid @RequestBody CreateVoucherRequest request) {
    VoucherEntity voucher = voucherService.createVoucher(request);
    VoucherResponse convertedVoucher = voucherMapper.entityToResponse(voucher);
    Map<String, Object> data = Map.of("voucher", convertedVoucher);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Tạo voucher thành công", data));
  }
}

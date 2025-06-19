package com.store.backend.size;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.backend.common.ApiResponse;
import com.store.backend.size.request.CreateSizeRequest;
import com.store.backend.size.request.UpdateSizeRequest;
import com.store.backend.size.response.SizeResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sizes")
public class SizeController {
  private final SizeService sizeService;

  @PostMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ApiResponse> createSize(@Valid @RequestBody CreateSizeRequest request) {
    SizeResponse convertedSize = sizeService.createSize(request);
    Map<String, Object> data = new HashMap<>();
    data.put("size", convertedSize);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Tạo kích cỡ sản phẩm thành công", data));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ApiResponse> updateSize(@PathVariable String id,
      @Valid @RequestBody UpdateSizeRequest request) {
    SizeResponse convertedSize = sizeService.updateSize(id, request);
    Map<String, Object> data = new HashMap<>();
    data.put("size", convertedSize);
    return ResponseEntity.ok(new ApiResponse("Sửa kích cỡ sản phẩm thành công", data));
  }
}

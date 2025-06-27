package com.store.backend.variant;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.backend.common.ApiResponse;
import com.store.backend.variant.mapper.VariantMapper;
import com.store.backend.variant.request.CreateVariantRequest;
import com.store.backend.variant.request.UpdateVariantRequest;
import com.store.backend.variant.response.VariantResponse;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@RequestMapping("/variants")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VariantController {
  VariantService variantService;
  VariantMapper variantMapper;

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<ApiResponse> createVariant(@Valid @RequestBody CreateVariantRequest request) {
    VariantEntity variant = variantService.createVariant(request);
    VariantResponse convertedVariant = variantMapper.entityToResponse(variant);
    Map<String, Object> data = new HashMap<>();
    data.put("variant", convertedVariant);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ApiResponse("Tạo biến thể của " + variant.getProduct().getTitle() + " thành công", data));
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<ApiResponse> updateVariant(@PathVariable String id,
      @Valid @RequestBody UpdateVariantRequest request) {
    VariantEntity variant = variantService.updateVariant(id, request);
    VariantResponse convertedVariant = variantMapper.entityToResponse(variant);
    Map<String, Object> data = new HashMap<>();
    data.put("variant", convertedVariant);
    return ResponseEntity
        .ok(new ApiResponse("Cập nhật biến thể của " + variant.getProduct().getTitle() + " thành công", data));
  }
}

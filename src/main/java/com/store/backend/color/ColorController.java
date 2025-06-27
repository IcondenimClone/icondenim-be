package com.store.backend.color;

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

import com.store.backend.color.request.CreateColorRequest;
import com.store.backend.color.request.UpdateColorRequest;
import com.store.backend.color.response.ColorResponse;
import com.store.backend.common.ApiResponse;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@RequestMapping("/colors")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ColorController {
  ColorService colorService;

  @PostMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ApiResponse> createColor(@Valid @RequestBody CreateColorRequest request) {
    ColorResponse convertedColor = colorService.createColor(request);
    Map<String, Object> data = new HashMap<>();
    data.put("color", convertedColor);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Tạo màu sắc mới thành công", data));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ApiResponse> updateColor(@PathVariable String id,
      @Valid @RequestBody UpdateColorRequest request) {
    ColorResponse convertedColor = colorService.updateColor(id, request);
    Map<String, Object> data = new HashMap<>();
    data.put("color", convertedColor);
    return ResponseEntity.ok(new ApiResponse("Cập nhật màu sắc mới thành công", data));
  }
}

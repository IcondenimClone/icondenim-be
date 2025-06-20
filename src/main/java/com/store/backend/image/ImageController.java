package com.store.backend.image;

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
import com.store.backend.image.mapper.ImageMapper;
import com.store.backend.image.request.CreateImageRequest;
import com.store.backend.image.request.UpdateImageRequest;
import com.store.backend.image.response.ImageResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {
  private final ImageService imageService;
  private final ImageMapper imageMapper;

  @PostMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ApiResponse> createImage(@Valid @RequestBody CreateImageRequest request) {
    ImageEntity image = imageService.createImage(request);
    ImageResponse convertedImage = imageMapper.entityToResponse(image);
    Map<String, Object> data = Map.of("image", convertedImage);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Tạo hình ảnh sản phẩm thành công", data));
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ApiResponse> updateImage(@PathVariable String id, @Valid @RequestBody UpdateImageRequest request) {
    ImageEntity image = imageService.updateImage(id, request);
    ImageResponse convertedImage = imageMapper.entityToResponse(image);
    Map<String, Object> data = Map.of("image", convertedImage);
    return ResponseEntity.ok(new ApiResponse("Cập nhật hình ảnh sản phẩm thành công", data));
  }
}

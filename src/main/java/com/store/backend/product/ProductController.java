package com.store.backend.product;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.backend.common.ApiResponse;
import com.store.backend.product.mapper.ProductMapper;
import com.store.backend.product.request.CreateProductRequest;
import com.store.backend.product.request.UpdateProductRequest;
import com.store.backend.product.response.ProductResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
  private final ProductService productService;
  private final ProductMapper productMapper;

  @PostMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
    ProductEntity product = productService.createProduct(request);
    ProductResponse convertedProduct = productMapper.entityToResponse(product);
    Map<String, Object> data = createProductResponse(convertedProduct);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Tạo sản phẩm thành công", data));
  }

  @GetMapping("/{slug}")
  public ResponseEntity<ApiResponse> getProductBySlug(@PathVariable String slug) {
    ProductEntity product = productService.getProductBySlug(slug);
    ProductResponse convertedProduct = productMapper.entityToResponse(product);
    Map<String, Object> data = createProductResponse(convertedProduct);
    return ResponseEntity.ok(new ApiResponse("Lấy sản phẩm thành công", data));
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ApiResponse> updateProduct(@PathVariable String id,
      @Valid @RequestBody UpdateProductRequest request) {
    ProductEntity product = productService.updateProduct(id, request);
    ProductResponse convertedProduct = productMapper.entityToResponse(product);
    Map<String, Object> data = createProductResponse(convertedProduct);
    return ResponseEntity.ok(new ApiResponse("Thay đổi sản phẩm thành công", data));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<ApiResponse> deleteProduct(@PathVariable String id) {
    productService.deleteProduct(id);
    return ResponseEntity.ok(new ApiResponse("Xóa sản phẩm thành công", null));
  }

  private Map<String, Object> createProductResponse(ProductResponse productResponse) {
    Map<String, Object> data = new HashMap<>();
    data.put("product", productResponse);
    return data;
  }
}

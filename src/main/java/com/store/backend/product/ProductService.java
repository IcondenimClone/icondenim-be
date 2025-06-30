package com.store.backend.product;

import com.store.backend.product.request.CreateProductRequest;
import com.store.backend.product.request.UpdateProductRequest;
import com.store.backend.product.response.PagedResponse;
import com.store.backend.product.response.ProductResponse;

public interface ProductService {
  PagedResponse<ProductResponse> getAllProducts(int page, int size);

  ProductEntity createProduct(CreateProductRequest request);

  ProductEntity getProductBySlug(String slug);

  ProductEntity updateProduct(String id, UpdateProductRequest request);

  void deleteProduct(String id);
}

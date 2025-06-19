package com.store.backend.product;

import com.store.backend.product.request.CreateProductRequest;
import com.store.backend.product.request.UpdateProductRequest;

public interface ProductService {
  ProductEntity createProduct(CreateProductRequest request);

  ProductEntity getProductBySlug(String slug);

  ProductEntity updateProduct(String id, UpdateProductRequest request);

  void deleteProduct(String id);
}

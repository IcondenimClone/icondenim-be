package com.store.backend.product;

import com.store.backend.product.request.CreateProductRequest;

public interface ProductService {
  ProductEntity createProduct(CreateProductRequest request);
}

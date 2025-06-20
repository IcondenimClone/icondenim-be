package com.store.backend.variant;

import com.store.backend.variant.request.CreateVariantRequest;
import com.store.backend.variant.request.UpdateVariantRequest;

public interface VariantService {
  VariantEntity createVariant(CreateVariantRequest request);

  VariantEntity updateVariant(String id, UpdateVariantRequest request);
}

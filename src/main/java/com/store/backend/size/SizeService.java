package com.store.backend.size;

import com.store.backend.size.request.CreateSizeRequest;
import com.store.backend.size.request.UpdateSizeRequest;
import com.store.backend.size.response.SizeResponse;

public interface SizeService {
  SizeResponse createSize(CreateSizeRequest request);

  SizeResponse updateSize(String id, UpdateSizeRequest request);
}

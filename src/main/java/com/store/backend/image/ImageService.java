package com.store.backend.image;

import com.store.backend.image.request.CreateImageRequest;
import com.store.backend.image.request.UpdateImageRequest;

public interface ImageService {
  ImageEntity createImage(CreateImageRequest request);

  ImageEntity updateImage(String id, UpdateImageRequest request);
}

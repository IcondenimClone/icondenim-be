package com.store.backend.color;

import com.store.backend.color.request.CreateColorRequest;
import com.store.backend.color.request.UpdateColorRequest;
import com.store.backend.color.response.ColorResponse;

public interface ColorService {
  ColorResponse createColor(CreateColorRequest request);

  ColorResponse updateColor(String id, UpdateColorRequest request);
}

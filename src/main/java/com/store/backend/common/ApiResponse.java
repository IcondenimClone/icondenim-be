package com.store.backend.common;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ApiResponse {
  private String message;
  private Map<String, Object> data;
}

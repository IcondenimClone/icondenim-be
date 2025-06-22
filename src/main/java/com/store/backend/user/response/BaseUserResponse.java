package com.store.backend.user.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseUserResponse {
  private String id;
  private String username;
  private String firstName;
  private String lastName;
}

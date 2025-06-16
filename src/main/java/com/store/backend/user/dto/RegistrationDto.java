package com.store.backend.user.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDto implements Serializable {
  private String email;
  private String username;
  private String password;
  private String otp;
  private int attempts;
}

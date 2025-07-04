package com.store.backend.user.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
  private String id;
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private LocalDate dob;
  private LocalDateTime createdAt;
}

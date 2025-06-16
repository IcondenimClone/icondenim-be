package com.store.backend.user.response;

import java.time.LocalDateTime;
import com.store.backend.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
  private String id;
  private String username;
  private String email;
  private UserRole role;
  private String firstName;
  private String lastName;
  private LocalDateTime createdAt;
}

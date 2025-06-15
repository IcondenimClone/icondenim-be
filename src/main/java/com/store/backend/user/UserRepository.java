package com.store.backend.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {
  UserEntity findByUsername(String username);
  
  UserEntity findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}
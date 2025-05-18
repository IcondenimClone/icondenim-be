package com.store.backend.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByUsername(String username);
  
  Optional<UserEntity> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}
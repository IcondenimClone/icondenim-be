package com.store.backend.user;

import org.springframework.stereotype.Service;

import com.store.backend.user.request.SignupRequest;
import com.store.backend.user.response.UserResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements IUserService {

  private final UserRepository userRepository;

  @Override
  public UserResponse signup(SignupRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("Email đã tồn tại");
    }

    if (userRepository.existsByUsername(request.getUsername())) {
      throw new IllegalArgumentException("Username đã tồn tại");
    }

    UserEntity user = UserEntity.builder().username(request.getUsername()).email(request.getEmail())
        .password(request.getPassword()).role(UserRole.USER).build();

    UserEntity savedUser = userRepository.save(user);

    return UserResponse.builder()
        .id(savedUser.getId())
        .username(savedUser.getUsername())
        .email(savedUser.getEmail())
        .firstName(savedUser.getFirstName())
        .lastName(savedUser.getLastName())
        .role(savedUser.getRole())
        .createdAt(savedUser.getCreatedAt())
        .updatedAt(savedUser.getUpdatedAt())
        .build();
  }
}

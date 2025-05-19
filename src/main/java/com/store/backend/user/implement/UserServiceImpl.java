package com.store.backend.user.implement;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.store.backend.exception.AlreadyExistsException;
import com.store.backend.user.UserEntity;
import com.store.backend.user.UserRepository;
import com.store.backend.user.UserService;
import com.store.backend.user.enums.UserRole;
import com.store.backend.user.request.SignupRequest;
import com.store.backend.user.response.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserResponse signup(SignupRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new AlreadyExistsException("Email đã tồn tại");
    }

    if (userRepository.existsByUsername(request.getUsername())) {
      throw new AlreadyExistsException("Username đã tồn tại");
    }

    UserEntity user = UserEntity.builder().username(request.getUsername()).email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword())).role(UserRole.USER).build();

    UserEntity savedUser = userRepository.save(user);

    return toUserResponse(savedUser);
  }

  public UserResponse getUserByUsername(String username) {
    UserEntity user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng có username: " + username));

    return toUserResponse(user);
  }

  private UserResponse toUserResponse(UserEntity user) {
    return UserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .role(user.getRole())
        .createdAt(user.getCreatedAt())
        .build();
  }
}

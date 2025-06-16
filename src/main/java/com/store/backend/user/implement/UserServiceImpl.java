package com.store.backend.user.implement;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.store.backend.exception.AlreadyExistsException;
import com.store.backend.exception.NotFoundException;
import com.store.backend.user.UserEntity;
import com.store.backend.user.UserRepository;
import com.store.backend.user.UserService;
import com.store.backend.user.enums.UserRole;
import com.store.backend.user.request.SigninRequest;
import com.store.backend.user.request.SignupRequest;
import com.store.backend.user.response.AuthResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  @Override
  public UserEntity signup(SignupRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new AlreadyExistsException("Email đã tồn tại");
    }

    if (userRepository.existsByUsername(request.getUsername())) {
      throw new AlreadyExistsException("Username đã tồn tại");
    }

    UserEntity user = UserEntity.builder().username(request.getUsername()).email(request.getEmail())
        .firstName(request.getFirstName()).lastName(request.getLastName())
        .password(passwordEncoder.encode(request.getPassword())).role(UserRole.USER).build();

    return userRepository.save(user);
  }

  @Override
  public UserEntity signin(SigninRequest request) {
    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    return userRepository.findByUsername(authentication.getName())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
  }

  @Override
  public UserEntity getUserById(String id) {
    return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
  }
}

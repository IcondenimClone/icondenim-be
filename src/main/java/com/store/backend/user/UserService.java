package com.store.backend.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.store.backend.exception.AlreadyExistsException;
import com.store.backend.user.enums.UserRole;
import com.store.backend.user.request.SignupRequest;
import com.store.backend.user.response.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements IUserService, UserDetailsService {

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

    return UserResponse.builder()
        .id(savedUser.getId())
        .username(savedUser.getUsername())
        .email(savedUser.getEmail())
        .firstName(savedUser.getFirstName())
        .lastName(savedUser.getLastName())
        .role(savedUser.getRole())
        .createdAt(savedUser.getCreatedAt())
        .build();
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    return new CustomUserDetails(user.getUsername(), user.getPassword(), user.getRole());
  }
}

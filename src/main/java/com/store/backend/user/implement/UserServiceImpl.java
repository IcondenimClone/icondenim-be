package com.store.backend.user.implement;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.store.backend.cache.RedisService;
import com.store.backend.exception.AlreadyExistsException;
import com.store.backend.exception.NotFoundException;
import com.store.backend.smtp.EmailService;
import com.store.backend.user.UserEntity;
import com.store.backend.user.UserRepository;
import com.store.backend.user.UserService;
import com.store.backend.user.dto.RegistrationDto;
import com.store.backend.user.enums.UserRole;
import com.store.backend.user.request.SignInRequest;
import com.store.backend.user.request.SignUpRequest;
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
  private final EmailService emailService;
  private final RedisService redisService;

  @Override
  public UserEntity signUp(SignUpRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new AlreadyExistsException("Email đã tồn tại");
    }

    if (userRepository.existsByUsername(request.getUsername())) {
      throw new AlreadyExistsException("Username đã tồn tại");
    }

    String otp = generateOtp();
    String registrationToken = generateUUID();
    String hashedPassword = passwordEncoder.encode(request.getPassword());

    RegistrationDto regData = RegistrationDto.builder().email(request.getEmail()).username(request.getUsername())
        .password(hashedPassword).otp(otp).attempts(0).build();
    
    redisService.saveObject(registrationToken, regData, 3, TimeUnit.MINUTES);

    emailService.sendVerifySignUpEmail(request.getEmail(), "Xác nhận Đăng ký tài khoản", otp);

    UserEntity user = UserEntity.builder().username(request.getUsername()).email(request.getEmail())
        .firstName(request.getFirstName()).lastName(request.getLastName())
        .password(passwordEncoder.encode(request.getPassword())).role(UserRole.USER).build();

    return userRepository.save(user);
  }

  @Override
  public UserEntity signIn(SignInRequest request) {
    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    return userRepository.findByUsername(authentication.getName())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
  }

  @Override
  public UserEntity getUserById(String id) {
    return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
  }

  private String generateOtp() {
    Random random = new Random();
    int otp = 100000 + random.nextInt(900000);
    return String.valueOf(otp);
  }

  private String generateUUID() {
    return UUID.randomUUID().toString();
  }
}

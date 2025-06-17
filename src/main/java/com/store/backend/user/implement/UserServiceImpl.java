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
import com.store.backend.exception.NotCorrectException;
import com.store.backend.exception.NotFoundException;
import com.store.backend.exception.TooManyException;
import com.store.backend.smtp.EmailService;
import com.store.backend.user.UserEntity;
import com.store.backend.user.UserRepository;
import com.store.backend.user.UserService;
import com.store.backend.user.dto.RegistrationDto;
import com.store.backend.user.enums.UserRole;
import com.store.backend.user.request.SignInRequest;
import com.store.backend.user.request.SignUpRequest;
import com.store.backend.user.request.VerifySignUpRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final EmailService emailService;
  private final RedisService redisService;

  @Override
  public String signUp(SignUpRequest request) {
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

    emailService.sendVerifySignUpEmail(request.getEmail(), "Xác nhận Đăng ký tài khoản", otp);

    String redisKey = redisService.setKey(registrationToken, ":signup:");
    redisService.saveObject(redisKey, regData, 3, TimeUnit.MINUTES);

    return registrationToken;
  }

  @Override
  @Transactional
  public UserEntity verifySignUp(VerifySignUpRequest request) {
    String redisKey = redisService.setKey(request.getRegistrationToken(), ":signup:");
    RegistrationDto regData = (RegistrationDto) redisService.getObject(redisKey);
    if (regData.getAttempts() >= 3) {
      redisService.deleteObject(redisKey);
      throw new TooManyException("Vượt quá lần thử mã đăng ký");
    }
    regData.setAttempts(regData.getAttempts() + 1);
    redisService.updateObject(redisKey, regData);
    if (!regData.getOtp().equals(request.getOtp())) {
      throw new NotCorrectException("Mã OTP không chính xác");
    }

    if (userRepository.existsByEmail(regData.getEmail())) {
      throw new AlreadyExistsException("Email đã tồn tại");
    }
    if (userRepository.existsByUsername(regData.getUsername())) {
      throw new AlreadyExistsException("Username đã tồn tại");
    }

    UserEntity user = UserEntity.builder().email(regData.getEmail()).username(regData.getUsername())
        .password(regData.getPassword()).role(UserRole.USER).build();

    redisService.deleteObject(redisKey);
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

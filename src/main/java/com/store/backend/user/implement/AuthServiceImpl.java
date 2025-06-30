package com.store.backend.user.implement;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.store.backend.cart.service.CartService;
import com.store.backend.exception.AlreadyExistsException;
import com.store.backend.exception.NotCorrectException;
import com.store.backend.exception.NotFoundException;
import com.store.backend.exception.TooManyException;
import com.store.backend.rabbitmq.PublishMessage;
import com.store.backend.redis.RedisService;
import com.store.backend.user.UserEntity;
import com.store.backend.user.UserRepository;
import com.store.backend.user.customs.CustomUserDetails;
import com.store.backend.user.dto.AuthEmailDto;
import com.store.backend.user.dto.ForgotPasswordDto;
import com.store.backend.user.dto.RegistrationDto;
import com.store.backend.user.enums.UserRole;
import com.store.backend.user.request.ChangePasswordRequest;
import com.store.backend.user.request.ForgotPasswordRequest;
import com.store.backend.user.request.ResetPasswordRequest;
import com.store.backend.user.request.SignInRequest;
import com.store.backend.user.request.SignUpRequest;
import com.store.backend.user.request.UpdateInfoRequest;
import com.store.backend.user.request.VerifyForgotPasswordRequest;
import com.store.backend.user.request.VerifySignUpRequest;
import com.store.backend.user.service.AuthService;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
  UserRepository userRepository;
  PasswordEncoder passwordEncoder;
  CartService cartService;
  AuthenticationManager authenticationManager;
  RedisService redisService;
  PublishMessage publishMessage;

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

    sendEmail(request.getEmail(), "Xác nhận Đăng ký tài khoản tại Icondenim", otp);

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

    cartService.createDefaultCart(user.getId());

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

  @Override
  public String forgotPassword(ForgotPasswordRequest request) {
    if (!userRepository.existsByEmail(request.getEmail())) {
      throw new NotFoundException("Người dùng không tồn tại");
    }
    String otp = generateOtp();
    String forgotPasswordToken = generateUUID();

    ForgotPasswordDto forgData = ForgotPasswordDto.builder().email(request.getEmail()).otp(otp).attempts(0).build();

    sendEmail(request.getEmail(), "Xác nhận Quên mật khẩu tại Icondenim", otp);

    String redisKey = redisService.setKey(forgotPasswordToken, ":forgot-password:");
    redisService.saveObject(redisKey, forgData, 3, TimeUnit.MINUTES);

    return forgotPasswordToken;
  }

  @Override
  public String verifyForgotPassword(VerifyForgotPasswordRequest request) {
    String redisKey = redisService.setKey(request.getForgotPasswordToken(), ":forgot-password:");
    ForgotPasswordDto forgData = (ForgotPasswordDto) redisService.getObject(redisKey);
    if (forgData.getAttempts() >= 3) {
      redisService.deleteObject(redisKey);
      throw new TooManyException("Vượt quá lần thử mã quên mật khẩu");
    }

    forgData.setAttempts(forgData.getAttempts() + 1);
    redisService.updateObject(redisKey, forgData);
    if (!forgData.getOtp().equals(request.getOtp())) {
      throw new NotCorrectException("Mã OTP không chính xác");
    }
    redisService.deleteObject(redisKey);

    String resetPasswordToken = generateUUID();
    redisKey = redisService.setKey(resetPasswordToken, ":reset-password:");
    redisService.saveString(redisKey, forgData.getEmail(), 3, TimeUnit.MINUTES);
    return resetPasswordToken;
  }

  @Override
  @Transactional
  public UserEntity resetPassword(ResetPasswordRequest request) {
    String redisKey = redisService.setKey(request.getResetPasswordToken(), ":reset-password:");
    String email = redisService.getString(redisKey);
    redisService.deleteString(redisKey);

    UserEntity user = userRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    return userRepository.save(user);
  }

  @Override
  @Transactional
  public UserEntity changePassword(CustomUserDetails userDetails, ChangePasswordRequest request) {
    UserEntity user = getUserById(userDetails.getId());
    if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
      throw new NotCorrectException("Mật khẩu cũ không khớp");
    }
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    return userRepository.save(user);
  }

  @Override
  @Transactional
  public UserEntity updateInfo(CustomUserDetails userDetails, UpdateInfoRequest request) {
    UserEntity user = getUserById(userDetails.getId());
    if (request.getFirstName() != null)
      user.setFirstName(request.getFirstName());
    if (request.getLastName() != null)
      user.setLastName(request.getLastName());
    if (request.getDob() != null)
      user.setDob(request.getDob());
    return userRepository.save(user);
  }

  private void sendEmail(String to, String subject, String otp) {
    AuthEmailDto message = AuthEmailDto.builder().to(to)
        .subject(subject).otp(otp).build();
    publishMessage.sendAuthEmail(message);
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

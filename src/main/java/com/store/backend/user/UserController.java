package com.store.backend.user;

import org.springframework.web.bind.annotation.RestController;
import com.store.backend.common.ApiResponse;
import com.store.backend.security.JwtService;
import com.store.backend.user.customs.CustomUserDetails;
import com.store.backend.user.enums.UserRole;
import com.store.backend.user.mapper.UserMapper;
import com.store.backend.user.request.ChangePasswordRequest;
import com.store.backend.user.request.ForgotPasswordRequest;
import com.store.backend.user.request.ResetPasswordRequest;
import com.store.backend.user.request.SignInRequest;
import com.store.backend.user.request.SignUpRequest;
import com.store.backend.user.request.UpdateInfoRequest;
import com.store.backend.user.request.VerifyForgotPasswordRequest;
import com.store.backend.user.request.VerifySignUpRequest;
import com.store.backend.user.response.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {
  private final UserService userService;
  private final JwtService jwtService;
  private final UserMapper userMapper;

  @Value("${server.servlet.context-path}")
  private String apiPrefix;

  @Value("${jwt.access-token-name}")
  private String accessTokenName;

  @Value("${jwt.refresh-token-name}")
  private String refreshTokenName;

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignUpRequest request) {
    String registrationToken = userService.signUp(request);
    Map<String, Object> data = new HashMap<>();
    data.put("registrationToken", registrationToken);
    return ResponseEntity.ok(new ApiResponse("Vui lòng kiểm tra mã xác thực đăng ký ở email", data));
  }

  @PostMapping("/signup/verify")
  public ResponseEntity<ApiResponse> verifySignUp(@Valid @RequestBody VerifySignUpRequest request,
      HttpServletResponse response) {
    UserEntity user = userService.verifySignUp(request);
    String userId = user.getId();
    UserRole role = user.getRole();

    String accessToken = jwtService.generateAccessToken(userId, role);
    String refreshToken = jwtService.generateRefreshToken(userId, role);

    setTokenCookie(response, accessTokenName, accessToken, "/", 15 * 60);
    setTokenCookie(response, refreshTokenName, refreshToken, apiPrefix + "/auth/refresh", 7 * 24 * 60 * 60);

    AuthResponse convertedUser = userMapper.entityToAuthResponse(user);
    Map<String, Object> data = createAuthResponse(convertedUser);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Đăng ký thành công", data));
  }

  @PostMapping("/signin")
  public ResponseEntity<ApiResponse> signin(@Valid @RequestBody SignInRequest request,
      HttpServletResponse response) {
    UserEntity user = userService.signIn(request);
    String userId = user.getId();
    UserRole role = user.getRole();

    String accessToken = jwtService.generateAccessToken(userId, role);
    String refreshToken = jwtService.generateRefreshToken(userId, role);

    setTokenCookie(response, accessTokenName, accessToken, "/", 15 * 60);
    setTokenCookie(response, refreshTokenName, refreshToken, apiPrefix + "/auth/refresh", 7 * 24 * 60 * 60);

    AuthResponse convertedUser = userMapper.entityToAuthResponse(user);
    Map<String, Object> data = createAuthResponse(convertedUser);
    return ResponseEntity.ok(new ApiResponse("Đăng nhập thành công", data));
  }

  @PostMapping("/signout")
  public ResponseEntity<ApiResponse> signOut(@AuthenticationPrincipal UserDetails userDetails,
      HttpServletResponse response) {
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Bạn chưa đăng nhập", null));
    }

    clearTokenCookie(response, accessTokenName, "/");
    clearTokenCookie(response, refreshTokenName, apiPrefix + "/auth/refresh");

    return ResponseEntity.ok(new ApiResponse("Đăng xuất thành công", null));
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Bạn chưa đăng nhập", null));
    }

    UserEntity user = userService.getUserById(userDetails.getId());
    AuthResponse convertedUser = userMapper.entityToAuthResponse(user);
    Map<String, Object> data = createAuthResponse(convertedUser);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse("Lấy thông tin người dùng thành công", data));
  }

  @GetMapping("/refresh")
  public ResponseEntity<ApiResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
    String token = extractRefreshTokenFromCookie(request);
    if (token == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ApiResponse("Không tìm thấy refresh token", null));
    }

    String currentUserId = jwtService.extractUserId(token);
    UserEntity user = userService.getUserById(currentUserId);

    if (!jwtService.isTokenValid(token, user)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Refresh token không hợp lệ", null));
    }

    String userId = user.getId();
    UserRole role = user.getRole();

    String accessToken = jwtService.generateAccessToken(userId, role);
    String refreshToken = jwtService.generateRefreshToken(userId, role);

    setTokenCookie(response, accessTokenName, accessToken, "/", 15 * 60);
    setTokenCookie(response, refreshTokenName, refreshToken, apiPrefix + "/auth/refresh", 7 * 24 * 60 * 60);

    return ResponseEntity.ok(new ApiResponse("Làm mới token thành công", null));
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
    String forgotPasswordToken = userService.forgotPassword(request);
    Map<String, Object> data = new HashMap<>();
    data.put("forgotPasswordToken", forgotPasswordToken);
    return ResponseEntity.ok(new ApiResponse("Vui lòng kiểm tra mã xác thực quên mật khẩu ở email", data));
  }

  @PostMapping("/forgot-password/verify")
  public ResponseEntity<ApiResponse> verifyForgotPassword(@Valid @RequestBody VerifyForgotPasswordRequest request) {
    String resetPasswordToken = userService.verifyForgotPassword(request);
    Map<String, Object> data = new HashMap<>();
    data.put("resetPasswordToken", resetPasswordToken);
    return ResponseEntity.ok(new ApiResponse("Xác thực quên mật khẩu thành công", data));
  }

  @PostMapping("/reset-password")
  public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request,
      HttpServletResponse response) {
    UserEntity user = userService.resetPassword(request);
    String userId = user.getId();
    UserRole role = user.getRole();

    String accessToken = jwtService.generateAccessToken(userId, role);
    String refreshToken = jwtService.generateRefreshToken(userId, role);

    setTokenCookie(response, accessTokenName, accessToken, "/", 15 * 60);
    setTokenCookie(response, refreshTokenName, refreshToken, apiPrefix + "/auth/refresh", 7 * 24 * 60 * 60);

    AuthResponse convertedUser = userMapper.entityToAuthResponse(user);
    Map<String, Object> data = createAuthResponse(convertedUser);
    return ResponseEntity.ok(new ApiResponse("Làm mới mật khẩu thành công", data));
  }

  @PutMapping("/change-password")
  public ResponseEntity<ApiResponse> changePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody ChangePasswordRequest request, HttpServletResponse response) {
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Bạn chưa đăng nhập", null));
    }

    UserEntity user = userService.changePassword(userDetails, request);
    String userId = user.getId();
    UserRole role = user.getRole();

    String accessToken = jwtService.generateAccessToken(userId, role);
    String refreshToken = jwtService.generateRefreshToken(userId, role);

    setTokenCookie(response, accessTokenName, accessToken, "/", 15 * 60);
    setTokenCookie(response, refreshTokenName, refreshToken, apiPrefix + "/auth/refresh", 7 * 24 * 60 * 60);

    AuthResponse convertedUser = userMapper.entityToAuthResponse(user);
    Map<String, Object> data = createAuthResponse(convertedUser);
    return ResponseEntity.ok(new ApiResponse("Cập nhật mật khẩu thành công", data));
  }

  @PutMapping("/update-info")
  public ResponseEntity<ApiResponse> updateInfo(@AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody UpdateInfoRequest request) {
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Bạn chưa đăng nhập", null));
    }

    UserEntity user = userService.updateInfo(userDetails, request);
    AuthResponse convertedUser = userMapper.entityToAuthResponse(user);
    Map<String, Object> data = createAuthResponse(convertedUser);
    return ResponseEntity.ok(new ApiResponse("Cập nhật thông tin thành công", data));
  }

  private Map<String, Object> createAuthResponse(AuthResponse authResponse) {
    Map<String, Object> data = new HashMap<>();
    data.put("user", authResponse);
    return data;
  }

  private void setTokenCookie(HttpServletResponse response, String name, String value, String path, int maxAge) {
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(true);
    cookie.setSecure(false);
    cookie.setPath(path);
    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);
  }

  private void clearTokenCookie(HttpServletResponse response, String name, String path) {
    Cookie cookie = new Cookie(name, null);
    cookie.setHttpOnly(true);
    cookie.setSecure(false);
    cookie.setPath(path);
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }

  private String extractRefreshTokenFromCookie(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (refreshTokenName.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
}

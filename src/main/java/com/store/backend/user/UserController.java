package com.store.backend.user;

import org.springframework.web.bind.annotation.RestController;
import com.store.backend.common.ApiResponse;
import com.store.backend.security.JwtService;
import com.store.backend.user.customs.CustomUserDetails;
import com.store.backend.user.enums.UserRole;
import com.store.backend.user.request.SigninRequest;
import com.store.backend.user.request.SignupRequest;
import com.store.backend.user.response.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

  @Value("${server.servlet.context-path}")
  private String apiPrefix;

  @Value("${jwt.access-token-name}")
  private String accessTokenName;

  @Value("${jwt.refresh-token-name}")
  private String refreshTokenName;

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest signupRequest,
      HttpServletResponse response) {
    UserEntity user = userService.signup(signupRequest);
    String userId = user.getId();
    UserRole role = user.getRole();

    String accessToken = jwtService.generateAccessToken(userId, role);
    String refreshToken = jwtService.generateRefreshToken(userId, role);

    setTokenCookie(response, accessTokenName, accessToken, "/", 15 * 60);
    setTokenCookie(response, refreshTokenName, refreshToken, apiPrefix + "/auth/refresh", 7 * 24 * 60 * 60);

    AuthResponse convertedUser = userService.convertToAuthResponse(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Đăng ký thành công", convertedUser));
  }

  @PostMapping("/signin")
  public ResponseEntity<ApiResponse> signin(@Valid @RequestBody SigninRequest signinRequest,
      HttpServletResponse response) {
    UserEntity user = userService.signin(signinRequest);
    String userId = user.getId();
    UserRole role = user.getRole();

    String accessToken = jwtService.generateAccessToken(userId, role);
    String refreshToken = jwtService.generateRefreshToken(userId, role);

    setTokenCookie(response, accessTokenName, accessToken, "/", 15 * 60);
    setTokenCookie(response, refreshTokenName, refreshToken, apiPrefix + "/auth/refresh", 7 * 24 * 60 * 60);

    AuthResponse convertedUser = userService.convertToAuthResponse(user);
    return ResponseEntity.ok(new ApiResponse("Đăng nhập thành công", convertedUser));
  }

  @PostMapping("/signout")
  public ResponseEntity<ApiResponse> signout(@AuthenticationPrincipal UserDetails userDetails,
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
    AuthResponse convertedUser = userService.convertToAuthResponse(user);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse("Lấy thông tin người dùng thành công", convertedUser));
  }

  @GetMapping("/refresh")
  public ResponseEntity<ApiResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
    String token = extractRefreshTokenFromCookie(request);
    if (token == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ApiResponse("Không tìm thấy thông tin refresh token", null));
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
        System.out.println(cookie.getName());
        if (refreshTokenName.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
}

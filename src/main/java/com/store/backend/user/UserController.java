package com.store.backend.user;

import org.springframework.web.bind.annotation.RestController;
import com.store.backend.common.ApiResponse;
import com.store.backend.security.JwtService;
import com.store.backend.user.enums.UserRole;
import com.store.backend.user.request.SigninRequest;
import com.store.backend.user.request.SignupRequest;
import com.store.backend.user.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {
  private final UserService userService;
  private final JwtService jwtService;

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest signupRequest,
      HttpServletResponse response) {
    UserResponse user = userService.signup(signupRequest);
    String userId = user.getId();
    UserRole role = user.getRole();

    String accessToken = jwtService.generateAccessToken(userId, role);
    String refreshToken = jwtService.generateRefreshToken(userId, role);

    setTokenCookie(response, "accessToken", accessToken, "/", 15 * 60);
    setTokenCookie(response, "refreshToken", refreshToken, "/auth/refresh-token", 7 * 24 * 60 * 60);

    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Đăng ký thành công", user));
  }

  @PostMapping("/signin")
  public ResponseEntity<ApiResponse> signin(@Valid @RequestBody SigninRequest signinRequest,
      HttpServletResponse response) {
    UserResponse user = userService.signin(signinRequest);
    String userId = user.getId();
    UserRole role = user.getRole();

    String accessToken = jwtService.generateAccessToken(userId, role);
    String refreshToken = jwtService.generateRefreshToken(userId, role);

    setTokenCookie(response, "accessToken", accessToken, "/", 15 * 60);
    setTokenCookie(response, "refreshToken", refreshToken, "/auth/refresh-token", 7 * 24 * 60 * 60);

    return ResponseEntity.ok(new ApiResponse("Đăng nhập thành công", user));
  }

  @PostMapping("/signout")
  public ResponseEntity<ApiResponse> signout(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) {
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Bạn chưa đăng nhập", null));
    }

    clearTokenCookie(response, "accessToken", "/");
    clearTokenCookie(response, "refreshToken", "/auth/refresh-token");

    return ResponseEntity.ok(new ApiResponse("Đăng xuất thành công", null));
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse> me(@AuthenticationPrincipal UserDetails userDetails) {
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Bạn chưa đăng nhập", null));
    }

    UserResponse user = userService.getUserByUsername(userDetails.getUsername());
    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Lấy thông tin người dùng thành công", user));
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

}

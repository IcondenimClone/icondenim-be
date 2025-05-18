package com.store.backend.user;

import org.springframework.web.bind.annotation.RestController;
import com.store.backend.common.ApiResponse;
import com.store.backend.security.JwtService;
import com.store.backend.user.request.SigninRequest;
import com.store.backend.user.request.SignupRequest;
import com.store.backend.user.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @PostMapping("/auth/signup")
  public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
    UserResponse user = userService.signup(signupRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Đăng ký thành công", user));
  }

  @PostMapping("/auth/signin")
  public ResponseEntity<ApiResponse> signin(@Valid @RequestBody SigninRequest signinRequest,
      HttpServletResponse response) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            signinRequest.getUsername(),
            signinRequest.getPassword()));

    String username = authentication.getName();
    UserResponse user = userService.getUserByUsername(username);
    String role = user.getRole().name();

    String accessToken = jwtService.generateAccessToken(username, role);
    String refreshToken = jwtService.generateRefreshToken(username);

    Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
    accessTokenCookie.setHttpOnly(true);
    accessTokenCookie.setSecure(false);
    accessTokenCookie.setPath("/");
    accessTokenCookie.setMaxAge(15 * 60);
    response.addCookie(accessTokenCookie);

    Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(false);
    refreshTokenCookie.setPath("/icondenim-be/auth/refresh-token");
    refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); 
    response.addCookie(refreshTokenCookie);

    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Đăng nhập thành công", user));
  }

  @GetMapping("/auth/me")
  public ResponseEntity<ApiResponse> me(@AuthenticationPrincipal UserDetails userDetails) {
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Bạn chưa đăng nhập", null));
    }

    UserResponse user = userService.getUserByUsername(userDetails.getUsername());
    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Lấy thông tin người dùng thành công", user));
  }
  
}

package com.store.backend.user;

import org.springframework.web.bind.annotation.RestController;

import com.store.backend.common.ApiResponse;
import com.store.backend.user.request.SignupRequest;
import com.store.backend.user.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.http.HttpStatus;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/auth/signup")
  public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest signupReq) {
    UserResponse user = userService.signup(signupReq);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Đăng ký thành công", user));
  }

}

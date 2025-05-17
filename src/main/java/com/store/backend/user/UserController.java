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
import static org.springframework.http.HttpStatus.CONFLICT;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/auth/signup")
  public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest signupReq) {
    try {
      UserResponse user = userService.signup(signupReq);
      return ResponseEntity.ok(new ApiResponse("Đăng ký thành công", user));
    } catch (Exception e) {
      return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
    }
  }

}

package com.store.backend.user;

import com.store.backend.user.request.SigninRequest;
import com.store.backend.user.request.SignupRequest;
import com.store.backend.user.response.UserResponse;

public interface UserService {
  UserResponse signup(SignupRequest request);

  UserResponse signin(SigninRequest request);

  UserResponse getUserByUsername(String username);
}

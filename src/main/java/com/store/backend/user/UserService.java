package com.store.backend.user;

import com.store.backend.user.request.SigninRequest;
import com.store.backend.user.request.SignupRequest;
import com.store.backend.user.response.AuthResponse;

public interface UserService {
  UserEntity signup(SignupRequest request);

  UserEntity signin(SigninRequest request);

  UserEntity getUserById(String id);

  AuthResponse convertToAuthResponse(UserEntity user);
}

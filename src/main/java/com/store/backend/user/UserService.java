package com.store.backend.user;

import com.store.backend.user.request.SignInRequest;
import com.store.backend.user.request.SignUpRequest;

public interface UserService {
  UserEntity signUp(SignUpRequest request);

  UserEntity signIn(SignInRequest request);

  UserEntity getUserById(String id);
}

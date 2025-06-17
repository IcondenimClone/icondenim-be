package com.store.backend.user;

import com.store.backend.user.request.SignInRequest;
import com.store.backend.user.request.SignUpRequest;
import com.store.backend.user.request.VerifySignUpRequest;

public interface UserService {
  String signUp(SignUpRequest request);

  UserEntity verifySignUp(VerifySignUpRequest request);

  UserEntity signIn(SignInRequest request);

  UserEntity getUserById(String id);
}

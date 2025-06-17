package com.store.backend.user;

import com.store.backend.user.customs.CustomUserDetails;
import com.store.backend.user.request.ChangePasswordRequest;
import com.store.backend.user.request.ForgotPasswordRequest;
import com.store.backend.user.request.ResetPasswordRequest;
import com.store.backend.user.request.SignInRequest;
import com.store.backend.user.request.SignUpRequest;
import com.store.backend.user.request.UpdateInfoRequest;
import com.store.backend.user.request.VerifyForgotPasswordRequest;
import com.store.backend.user.request.VerifySignUpRequest;

public interface UserService {
  String signUp(SignUpRequest request);

  UserEntity verifySignUp(VerifySignUpRequest request);

  UserEntity signIn(SignInRequest request);

  UserEntity getUserById(String id);

  String forgotPassword(ForgotPasswordRequest request);

  String verifyForgotPassword(VerifyForgotPasswordRequest request);

  UserEntity resetPassword(ResetPasswordRequest request);

  UserEntity changePassword(CustomUserDetails userDetails, ChangePasswordRequest request);

  UserEntity updateInfo(CustomUserDetails userDetails, UpdateInfoRequest request);
}

package com.store.backend.user;

import com.store.backend.user.request.SignupRequest;
import com.store.backend.user.response.UserResponse;

public interface IUserService {
  UserResponse signup(SignupRequest request);
} 

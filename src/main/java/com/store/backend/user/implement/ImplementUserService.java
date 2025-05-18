package com.store.backend.user.implement;

import com.store.backend.user.request.SignupRequest;
import com.store.backend.user.response.UserResponse;

public interface ImplementUserService {
  UserResponse signup(SignupRequest request);
} 

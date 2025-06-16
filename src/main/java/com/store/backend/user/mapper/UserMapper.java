package com.store.backend.user.mapper;

import org.mapstruct.Mapper;

import com.store.backend.user.UserEntity;
import com.store.backend.user.response.AuthResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
  AuthResponse entityToAuthResponse(UserEntity user);
}

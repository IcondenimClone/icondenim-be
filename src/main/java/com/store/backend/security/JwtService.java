package com.store.backend.security;

import java.util.function.Function;

import com.store.backend.user.UserEntity;
import com.store.backend.user.enums.UserRole;

import io.jsonwebtoken.Claims;

public interface JwtService {
  String generateAccessToken(String userId, UserRole role);

  String generateRefreshToken(String userId, UserRole role);

  boolean isTokenValid(String token, UserEntity user);

  String extractUserId(String token);

  UserRole extractRole(String token);

  <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
}

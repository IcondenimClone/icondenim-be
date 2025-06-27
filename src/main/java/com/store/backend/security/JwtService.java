package com.store.backend.security;

import java.util.function.Function;

import com.store.backend.user.UserEntity;
import com.store.backend.user.enums.UserRole;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface JwtService {
  String generateAccessToken(String userId, UserRole role);

  String generateRefreshToken(String userId, UserRole role);

  String generateGuestToken(String guestId);

  boolean isTokenValid(String token, UserEntity user);

  String extractUserId(String token);

  UserRole extractRole(String token);

  String extractGuestId(String token);

  <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

  String extractTokenFromCookie(HttpServletRequest request, String tokenName);

  void setTokenCookie(HttpServletResponse response, String name, String value, String path, int maxAge);

  void clearTokenCookie(HttpServletResponse response, String name, String path);
}

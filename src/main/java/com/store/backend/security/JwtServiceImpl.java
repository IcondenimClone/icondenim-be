package com.store.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.store.backend.user.UserEntity;
import com.store.backend.user.enums.UserRole;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {
  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.access-token-expiration:15}")
  private long accessTokenExpirationMinutes;

  @Value("${jwt.refresh-token-expiration:7}")
  private long refreshTokenExpirationDays;

  @Override
  public String generateAccessToken(String userId, UserRole role) {
    return generateToken(userId, role, accessTokenExpirationMinutes, ChronoUnit.MINUTES);
  }

  @Override
  public String generateRefreshToken(String userId, UserRole role) {
    return generateToken(userId, role, refreshTokenExpirationDays, ChronoUnit.DAYS);
  }

  private String generateToken(String userId, UserRole role, long expiration, ChronoUnit unit) {
    Instant now = Instant.now();
    Instant expirationTime = now.plus(expiration, unit);

    return Jwts.builder()
        .subject(userId)
        .claim("role", role.name())
        .issuedAt(Date.from(now))
        .expiration(Date.from(expirationTime))
        .signWith(getSigningKey(), Jwts.SIG.HS256)
        .compact();
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(Base64.getEncoder().encodeToString(secret.getBytes()));
    return Keys.hmacShaKeyFor(keyBytes);
  }

  @Override
  public boolean isTokenValid(String token, UserEntity user) {
    final String userId = extractUserId(token);
    final UserRole tokenRole = extractRole(token);
    return (userId.equals(user.getId())) && (tokenRole.equals(user.getRole())) && !isTokenExpired(token);
  }

  @Override
  public String extractUserId(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  @Override
  public UserRole extractRole(String token) {
    String roleName = extractClaim(token, claims -> claims.get("role", String.class));
    return UserRole.valueOf(roleName);
  }

  @Override
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}

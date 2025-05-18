package com.store.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.access-token-expiration:15}")
  private long accessTokenExpirationMinutes;

  @Value("${jwt.refresh-token-expiration:7}")
  private long refreshTokenExpirationDays;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String generateAccessToken(String username, String role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role);
    return generateToken(username, claims, accessTokenExpirationMinutes, ChronoUnit.MINUTES);
  }

  public String generateRefreshToken(String username) {
    return generateToken(username, Map.of(), refreshTokenExpirationDays, ChronoUnit.DAYS);
  }

  private String generateToken(String subject, Map<String, Object> claims, long expiration, ChronoUnit unit) {
    Instant now = Instant.now();
    Instant expirationTime = now.plus(expiration, unit);

    return Jwts.builder()
        .subject(subject)
        .claims(claims)
        .issuedAt(Date.from(now))
        .expiration(Date.from(expirationTime))
        .signWith(getSigningKey(), Jwts.SIG.HS256)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
      return true;
    } catch (JwtException e) {
      return false;
    }
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public String extractRole(String token) {
    return extractClaim(token, claims -> claims.get("role", String.class));
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}

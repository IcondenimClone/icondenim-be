package com.store.backend.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.store.backend.user.UserEntity;
import com.store.backend.user.UserRepository;
import com.store.backend.user.customs.CustomUserDetailsService;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final CustomUserDetailsService customUserDetailsService;
  private final UserRepository userRepository;

  @Value("${jwt.token.cookie-name:accessToken}")
  private String tokenCookieName;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    String token = extractTokenFromCookie(request);
    if (token == null) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String userId = jwtService.extractUserID(token);
      if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user != null && jwtService.isTokenValid(token, user)) {
          UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
              userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
    } catch (JwtException e) {
      log.warn("Invalid JWT token: {}", e.getMessage());
    } catch (Exception e) {
      log.error("Error processing JWT authentication: {}", e.getMessage(), e);
    }

    filterChain.doFilter(request, response);
  }

  private String extractTokenFromCookie(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (tokenCookieName.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
}
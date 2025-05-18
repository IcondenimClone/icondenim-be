package com.store.backend.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.store.backend.user.customs.CustomUserDetailsService;
import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  private final JwtService jwtService;
  private final CustomUserDetailsService customUserDetailsService;

  @Value("${jwt.token.cookie-name:accessToken}")
  private String tokenCookieName;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = extractToken(request);
    if (!StringUtils.hasText(token)) {
      logger.debug("No JWT token found in request");
      filterChain.doFilter(request, response);
      return;
    }

    try {
      jwtService.validateToken(token);
      String username = jwtService.extractUsername(token);

      UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities());
      authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(authToken);
      SecurityContextHolder.setContext(context);

      logger.debug("Successfully authenticated user: {}", username);
    } catch (JwtException e) {
      logger.warn("Invalid JWT token: {}", e.getMessage());
    } catch (Exception e) {
      logger.error("Error processing JWT authentication: {}", e.getMessage(), e);
    }

    filterChain.doFilter(request, response);
  }

  private String extractToken(HttpServletRequest request) {
    String token = extractTokenFromCookie(request);
    if (StringUtils.hasText(token)) {
      return token;
    }
    return null;
  }


  private String extractTokenFromCookie(HttpServletRequest request) {
    if (request.getCookies() == null) {
      return null;
    }

    return Arrays.stream(request.getCookies())
        .filter(cookie -> tokenCookieName.equals(cookie.getName()))
        .map(Cookie::getValue)
        .filter(StringUtils::hasText)
        .findFirst()
        .orElse(null);
  }
}
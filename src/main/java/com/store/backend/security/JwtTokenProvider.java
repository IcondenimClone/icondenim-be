// package com.store.backend.security;

// import java.util.Date;
// import java.util.stream.Collectors;

// import javax.crypto.SecretKey;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Authentication;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import com.store.backend.user.UserEntity;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.io.Decoders;
// import io.jsonwebtoken.security.Keys;
// import jakarta.servlet.http.Cookie;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// public class JwtTokenProvider {

//   private final SecretKey key;
//   private final long accessTokenValidity;
//   private final long refreshTokenValidity;

//   public JwtTokenProvider(
//       @Value("${jwt.secret}") String secret,
//       @Value("${jwt.access-token-validity}") long accessTokenValidity,
//       @Value("${jwt.refresh-token-validity}") long refreshTokenValidity) {
//     this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
//     this.accessTokenValidity = accessTokenValidity;
//     this.refreshTokenValidity = refreshTokenValidity;
//   }

//   public void createTokenCookie(Authentication auth, HttpServletResponse response) {
//     String username = auth.getName();
//     String authorities = auth.getAuthorities().stream()
//         .map(GrantedAuthority::getAuthority)
//         .collect(Collectors.joining(","));

//     String accessToken = Jwts.builder()
//         .subject(username)
//         .claim("auth", authorities)
//         .issuedAt(new Date())
//         .expiration(new Date(System.currentTimeMillis() + accessTokenValidity))
//         .signWith(key)
//         .compact();

//     String refreshToken = Jwts.builder()
//         .subject(username)
//         .issuedAt(new Date())
//         .expiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
//         .signWith(key)
//         .compact();

//     addCookie(response, "access_token", accessToken, (int) (accessTokenValidity / 1000));
//     addCookie(response, "refresh_token", refreshToken, (int) (refreshTokenValidity / 1000));
//   }

//   private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
//     Cookie cookie = new Cookie(name, value);
//     cookie.setHttpOnly(true);
//     cookie.setSecure(true);
//     cookie.setPath("/");
//     cookie.setMaxAge(maxAge);
//     response.addCookie(cookie);
//   }

//   public Authentication getAuthentication(String token) {
//     Claims claims = Jwts.parser()
//         .verifyWith(key)
//         .build()
//         .parseSignedClaims(token)
//         .getPayload();

//     Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth", String.class).split(","))
//         .filter(auth -> !auth.trim().isEmpty())
//         .map(SimpleGrantedAuthority::new)
//         .collect(Collectors.toList());

//     User principal = new UserEntity(claims.getSubject(), "", authorities);
//     return new UsernamePasswordAuthenticationToken(principal, token, authorities);
//   }

//   public boolean validateToken(String token) {
//     try {
//       Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
//       return true;
//     } catch (Exception e) {
//       return false;
//     }
//   }

//   public String getTokenFromCookies(HttpServletRequest request, String cookieName) {
//     Cookie[] cookies = request.getCookies();
//     if (cookies != null) {
//       for (Cookie cookie : cookies) {
//         if (cookieName.equals(cookie.getName())) {
//           return cookie.getValue();
//         }
//       }
//     }
//     return null;
//   }

//   public void clearTokenCookies(HttpServletResponse response) {
//     Cookie accessTokenCookie = new Cookie("access_token", "");
//     accessTokenCookie.setMaxAge(0);
//     accessTokenCookie.setPath("/");

//     Cookie refreshTokenCookie = new Cookie("refresh_token", "");
//     refreshTokenCookie.setMaxAge(0);
//     refreshTokenCookie.setPath("/");

//     response.addCookie(accessTokenCookie);
//     response.addCookie(refreshTokenCookie);
//   }
// }

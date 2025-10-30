package edu.dosw.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
  private final Key key;
  private final long expirationTime;

  public JwtUtil(
      @Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expirationTime) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
    this.expirationTime = expirationTime;
  }

  public String generateToken(String userId, String email) {
    return Jwts.builder()
        .setSubject(email)
        .claim("userId", userId)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
        .signWith(key)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (JwtException e) {
      logger.warn("Invalid JWT: {}", e.getMessage());
      return false;
    }
  }

  public String extractUserId(String token) {
    return extractAllClaims(token).get("userId", String.class);
  }

  public String extractEmail(String token) {
    return extractAllClaims(token).getSubject();
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }
}

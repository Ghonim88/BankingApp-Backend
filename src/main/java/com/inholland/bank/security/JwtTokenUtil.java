package com.inholland.bank.security;

import io.jsonwebtoken.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

@Component
public class JwtTokenUtil {

  @Value("${JWT_SECRET}")
  private String jwtSecret;


  @Value("${JWT_EXPIRATION_MS}")
  private int jwtExpirationMs;
  // This will generate a 512-bit key for HS512
  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Generate a JWT token from authenticated user details.
   * Adds custom claims such as user role and user ID.
   */
  public String generateToken(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();


    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .claim("role", userDetails.getUser().getUserRole())
        .claim("userId", userDetails.getUser().getUserId())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(getSigningKey())
        .compact();
  }

  public String getUserNameFromJwtToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  public String getUserRoleFromJwtToken(String token) {
    return getClaimFromToken(token, claims -> claims.get("role", String.class));
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

   //Parse the token and extract all claims.
  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Boolean isTokenExpired(String token) {
    final Date expiration = getClaimFromToken(token, Claims::getExpiration);
    return expiration.before(new Date());
  }

  public Boolean validateJwtToken(String token) {
    try {
      return !isTokenExpired(token);
    } catch (ExpiredJwtException e) {
      System.out.println("JWT token is expired: " + e.getMessage());
    } catch (SignatureException e) {
      System.out.println("Invalid JWT signature: " + e.getMessage());
    } catch (MalformedJwtException e) {
      System.out.println("Invalid JWT token: " + e.getMessage());
    } catch (UnsupportedJwtException e) {
      System.out.println("JWT token is unsupported: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      System.out.println("JWT claims string is empty: " + e.getMessage());
    }

    return false;
  }
}

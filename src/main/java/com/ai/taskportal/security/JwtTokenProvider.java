package com.ai.taskportal.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMs;

    private final UserDetailsService userDetailsService;

    /**
     * Generate JWT token from Authentication (includes authorities)
     */
//    public String generateAccessToken(Authentication authentication) {
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
//        SecretKey key = getSigningKey();
//
//        String authorities = userDetails.getAuthorities().stream()
//                .map(auth -> auth.getAuthority())
//                .collect(Collectors.joining(","));
//
//        return Jwts.builder()
//                .subject(userDetails.getUsername())
//                .issuedAt(now)
//                .expiration(expiryDate)
//                .claim("authorities", authorities)
//                .signWith(key)
//                .compact();
//    }

    /**
     * Generate JWT token from username
     */
    public String generateAccessToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        SecretKey key = getSigningKey();

        // Try to load user details if service is available
        String authorities = "";
        if (userDetailsService != null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                authorities = userDetails.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .collect(Collectors.joining(","));
            } catch (Exception ex) {
                log.warn("Could not load authorities for user: {}", email, ex);
            }
        }

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("authorities", authorities)
                .signWith(key)
                .compact();
    }

    /**
     * Generate refresh token
     */
    public String generateRefreshToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);
        SecretKey key = getSigningKey();

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("type", "refresh")
                .signWith(key)
                .compact();
    }

    /**
     * Get username from JWT token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * Get authorities from JWT token
     */
    public String getAuthoritiesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Object authorities = claims.get("authorities");
        return authorities != null ? authorities.toString() : "";
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("JWT validation error: {}", ex.getMessage());
        }

        return false;
    }

    /**
     * Get expiration time from token
     */
    public long getExpirationTime(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getExpiration().getTime();
    }

    /**
     * Create signing key
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }
}
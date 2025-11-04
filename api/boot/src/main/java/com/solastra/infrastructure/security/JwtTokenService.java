package com.solastra.infrastructure.security;

import com.solastra.domain.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtTokenService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtTokenService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Generate a JWT token for a user
     *
     * @param userId The user ID
     * @param email The user email
     * @param accountId The account ID
     * @param role The user role
     * @return The JWT token
     */
    public String generateToken(String userId, String email, String accountId, UserRole role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("accountId", accountId);
        claims.put("role", role.name());

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Validate a JWT token
     *
     * @param token The JWT token
     * @return true if valid
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract claims from a JWT token
     *
     * @param token The JWT token
     * @return The claims
     */
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extract email (subject) from token
     *
     * @param token The JWT token
     * @return The email
     */
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extract user ID from token
     *
     * @param token The JWT token
     * @return The user ID
     */
    public String extractUserId(String token) {
        return extractClaims(token).get("userId", String.class);
    }

    /**
     * Extract account ID from token
     *
     * @param token The JWT token
     * @return The account ID
     */
    public String extractAccountId(String token) {
        return extractClaims(token).get("accountId", String.class);
    }

    /**
     * Extract role from token
     *
     * @param token The JWT token
     * @return The role
     */
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }
}
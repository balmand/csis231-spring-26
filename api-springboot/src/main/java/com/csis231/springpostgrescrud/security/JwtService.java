package com.csis231.springpostgrescrud.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    public static final String ROLE_CLAIM = "role";

    private final SecretKey key;
    private final long expirationSeconds;

    public JwtService(
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.expiration-seconds}") long expirationSeconds
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(String subject) {
        return generateToken(subject, null);
    }

    public String generateToken(String subject, String role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationSeconds);

        Map<String, Object> claims = new HashMap<>();
        if (role != null && !role.isBlank()) {
            claims.put(ROLE_CLAIM, role);
        }

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public String extractSubject(String token) {
        return parseAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        Object value = parseAllClaims(token).get(ROLE_CLAIM);
        return value == null ? null : String.valueOf(value);
    }

    public boolean isTokenValid(String token) {
        try {
            parseAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}


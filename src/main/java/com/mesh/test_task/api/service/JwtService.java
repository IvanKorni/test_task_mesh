package com.mesh.test_task.api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private static final String USER_ID_CLAIM = "USER_ID";

    private final SecretKey signingKey;
    private final long expiration;

    public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expiration) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiration = expiration;
    }

    public String generateToken(Long userId) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claim(USER_ID_CLAIM, userId)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(signingKey)
                .compact();
    }

    public Long extractUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Number userId = claims.get(USER_ID_CLAIM, Number.class);

        if (userId == null) {
            throw new IllegalArgumentException("JWT does not contain USER_ID claim");
        }

        return userId.longValue();
    }
}

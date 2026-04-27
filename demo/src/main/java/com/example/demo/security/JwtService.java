package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// JWT トークンの生成と検証を行うサービス
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret:sunseer-bento-reservation-system-jwt-secret-key-2026}") String secret,
                      @Value("${app.jwt.expiration-ms:86400000}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    // ユーザー用 JWT トークンを生成
    public String generateUserToken(Long userId, String name) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("userType", "USER")
                .claim("name", name)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    // 管理者用 JWT トークンを生成
    public String generateAdminToken(Long adminId, String name) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(String.valueOf(adminId))
                .claim("userType", "ADMIN")
                .claim("name", name)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    // JWT トークンを検証して Claims を取得（無効なら null）
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException exception) {
            return null;
        }
    }

    // トークンからユーザーIDを取得
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return Long.valueOf(claims.getSubject());
    }

    // トークンからユーザータイプを取得
    public String getUserType(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return claims.get("userType", String.class);
    }

    // トークンからユーザー名を取得
    public String getUserName(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return claims.get("name", String.class);
    }
}
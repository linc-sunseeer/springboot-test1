package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.demo.entity.EmailToken;
import com.example.demo.mapper.EmailTokenMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class EmailTokenService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final EmailTokenMapper emailTokenMapper;

    public EmailTokenService(EmailTokenMapper emailTokenMapper) {
        this.emailTokenMapper = emailTokenMapper;
    }

    public String createToken(Long userId, String type) {
        if (userId == null || !StringUtils.hasText(type)) {
            throw new IllegalArgumentException("トークン作成に必要な情報が不足しています");
        }

        invalidateActiveTokens(userId, type);

        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        EmailToken emailToken = EmailToken.builder()
                .userId(userId)
                .tokenHash(hash(rawToken))
                .type(type)
                .expiresAt(LocalDateTime.now().plusMinutes("RESET_PASSWORD".equals(type) ? 30 : 1440))
                .createdAt(LocalDateTime.now())
                .build();
        emailTokenMapper.insert(emailToken);
        return rawToken;
    }

    public EmailToken consumeToken(String rawToken, String expectedType) {
        if (!StringUtils.hasText(rawToken) || !StringUtils.hasText(expectedType)) {
            throw new IllegalArgumentException("無効なトークンです");
        }

        EmailToken emailToken = emailTokenMapper.selectOne(new LambdaQueryWrapper<EmailToken>()
                .eq(EmailToken::getTokenHash, hash(rawToken))
                .eq(EmailToken::getType, expectedType)
                .isNull(EmailToken::getUsedAt)
                .last("limit 1"));

        if (emailToken == null || emailToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("無効なトークンです");
        }

        emailToken.setUsedAt(LocalDateTime.now());
        emailTokenMapper.updateById(emailToken);
        return emailToken;
    }

    private void invalidateActiveTokens(Long userId, String type) {
        emailTokenMapper.update(null, new LambdaUpdateWrapper<EmailToken>()
                .eq(EmailToken::getUserId, userId)
                .eq(EmailToken::getType, type)
                .isNull(EmailToken::getUsedAt)
                .set(EmailToken::getUsedAt, LocalDateTime.now()));
    }

    private String hash(String rawToken) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = messageDigest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : hashed) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 を利用できません", e);
        }
    }
}

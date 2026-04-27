package com.example.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public Long requireUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("ログイン情報を確認できません。再度ログインしてください。");
        }

        try {
            return Long.valueOf(authentication.getPrincipal().toString());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("ログイン情報を確認できません。再度ログインしてください。", exception);
        }
    }
}

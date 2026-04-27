package com.example.demo.service;

import com.example.demo.entity.EmailToken;
import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

// 這個 Service 專門處理忘記密碼 / 重設密碼流程
@Service
public class PasswordResetService {

    private final UserService userService;
    private final EmailTokenService emailTokenService;
    private final MailService mailService;
    private final String frontendBaseUrl;

    public PasswordResetService(UserService userService,
                                EmailTokenService emailTokenService,
                                MailService mailService,
                                @Value("${app.frontend-base-url:http://localhost:5174}") String frontendBaseUrl) {
        this.userService = userService;
        this.emailTokenService = emailTokenService;
        this.mailService = mailService;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    // 建立 reset token
    public void createResetToken(String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            return;
        }

        sendResetMail(user);
    }

    public void createResetTokenForUser(Long userId) {
        User user = userService.getUserById(userId);
        sendResetMail(user);
    }

    private void sendResetMail(User user) {
        String token = emailTokenService.createToken(user.getId(), "RESET_PASSWORD");
        String resetUrl = frontendBaseUrl + "/reset-password?token=" + token;
        String body = "パスワード変更のご案内です。\n\n以下のリンクから新しいパスワードを設定してください。\n" + resetUrl;
        mailService.sendMail(user.getEmail(), "パスワード変更の確認", body);
    }
    // 用 token 重設密碼
    public void resetPassword(String token, String newPassword) {
        if (!StringUtils.hasText(newPassword)) {
            throw new IllegalArgumentException("パスワードを入力してください");
        }

        EmailToken emailToken = emailTokenService.consumeToken(token, "RESET_PASSWORD");
        User user = userService.getUserById(emailToken.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("無効なトークンです");
        }

        userService.updatePasswordByEmail(user.getEmail(), newPassword);
    }
}

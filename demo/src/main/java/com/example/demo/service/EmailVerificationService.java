package com.example.demo.service;

import com.example.demo.entity.EmailToken;
import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationService {

    private final EmailTokenService emailTokenService;
    private final UserService userService;
    private final MailService mailService;
    private final String frontendBaseUrl;

    @Autowired
    public EmailVerificationService(EmailTokenService emailTokenService,
                                    UserService userService,
                                    MailService mailService,
                                    @Value("${app.frontend-base-url:http://localhost:5174}") String frontendBaseUrl) {
        this.emailTokenService = emailTokenService;
        this.userService = userService;
        this.mailService = mailService;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    public void sendVerificationEmail(User user) {
        if (user == null || user.getId() == null || user.getEmail() == null) {
            return;
        }

        String token = emailTokenService.createToken(user.getId(), "VERIFY_EMAIL");
        String verifyUrl = frontendBaseUrl + "/verify-email?token=" + token;
        String name = user.getName() == null || user.getName().trim().isEmpty() ? "ご利用者" : user.getName().trim();
        String body = name + "様\n\n"
                + "弁当予約管理システムへのご登録ありがとうございます。\n"
                + "以下のリンクを開いて、メール認証を完了してください。\n\n"
                + verifyUrl + "\n\n"
                + "認証完了後にログインしてご利用を開始できます。";
        if (mailService != null) {
            mailService.sendMail(user.getEmail(), "【弁当予約管理システム】メール認証のお願い", body);
        }
    }

    public void verifyEmail(String token) {
        EmailToken emailToken = emailTokenService.consumeToken(token, "VERIFY_EMAIL");
        userService.markEmailVerified(emailToken.getUserId());
    }
}

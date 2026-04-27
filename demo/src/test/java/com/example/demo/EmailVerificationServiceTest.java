package com.example.demo;

import com.example.demo.entity.EmailToken;
import com.example.demo.entity.User;
import com.example.demo.service.EmailTokenService;
import com.example.demo.service.EmailVerificationService;
import com.example.demo.service.MailService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock
    private EmailTokenService emailTokenService;

    @Mock
    private UserService userService;

    @Mock
    private MailService mailService;

    @Test
    void verifyEmailMarksUserAsVerifiedAndConsumesToken() {
        EmailVerificationService service = new EmailVerificationService(emailTokenService, userService, mailService, "http://localhost:5174");
        EmailToken token = EmailToken.builder()
                .userId(7L)
                .type("VERIFY_EMAIL")
                .build();

        when(emailTokenService.consumeToken("verify-token", "VERIFY_EMAIL")).thenReturn(token);
        service.verifyEmail("verify-token");

        verify(emailTokenService).consumeToken("verify-token", "VERIFY_EMAIL");
        verify(userService).markEmailVerified(7L);
    }
}

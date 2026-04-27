package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.service.EmailTokenService;
import com.example.demo.service.MailService;
import com.example.demo.service.PasswordResetService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private EmailTokenService emailTokenService;

    @Mock
    private MailService mailService;

    @Test
    void createResetTokenSendsResetMailOnlyForExistingUser() {
        PasswordResetService service = new PasswordResetService(userService, emailTokenService, mailService,
                "http://localhost:5174");
        User user = User.builder().id(3L).email("user@example.com").name("User").build();

        when(userService.findByEmail("user@example.com")).thenReturn(user);
        when(emailTokenService.createToken(3L, "RESET_PASSWORD")).thenReturn("reset-token");

        service.createResetToken("user@example.com");

        verify(emailTokenService).createToken(3L, "RESET_PASSWORD");
        verify(mailService).sendMail(eq("user@example.com"), contains("パスワード"), contains("reset-token"));
    }

    @Test
    void createResetTokenSilentlyReturnsWhenUserDoesNotExist() {
        PasswordResetService service = new PasswordResetService(userService, emailTokenService, mailService,
                "http://localhost:5174");

        when(userService.findByEmail("missing@example.com")).thenReturn(null);

        service.createResetToken("missing@example.com");

        verify(emailTokenService, never()).createToken(anyLong(), anyString());
        verify(mailService, never()).sendMail(anyString(), anyString(), anyString());
    }

    @Test
    void createResetTokenForUserSendsResetMail() {
        PasswordResetService service = new PasswordResetService(userService, emailTokenService, mailService,
                "http://localhost:5174");
        User user = User.builder().id(7L).email("user@example.com").name("User").build();

        when(userService.getUserById(7L)).thenReturn(user);
        when(emailTokenService.createToken(7L, "RESET_PASSWORD")).thenReturn("reset-token");

        service.createResetTokenForUser(7L);

        verify(emailTokenService).createToken(7L, "RESET_PASSWORD");
        verify(mailService).sendMail(eq("user@example.com"), contains("パスワード"), contains("reset-token"));
    }
}

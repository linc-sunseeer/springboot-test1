package com.example.demo;

import com.example.demo.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
class MailServiceTest {

    @Test
    void skipsSendWhenMailDisabled() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        @SuppressWarnings("unchecked")
        ObjectProvider<JavaMailSender> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(mailSender);

        MailService mailService = new MailService(provider, false, "sender@example.com");

        mailService.sendMail("user@example.com", "subject", "body");

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void skipsSendWhenMailSenderUnavailable() {
        @SuppressWarnings("unchecked")
        ObjectProvider<JavaMailSender> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(null);

        MailService mailService = new MailService(provider, true, "sender@example.com");

        mailService.sendMail("user@example.com", "subject", "body");
    }

    @Test
    void logsConfigurationErrorWhenMailEnabledButFromAddressBlank(CapturedOutput output) {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        @SuppressWarnings("unchecked")
        ObjectProvider<JavaMailSender> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(mailSender);

        new MailService(provider, true, "   ");

        org.junit.jupiter.api.Assertions.assertTrue(output.getOut().contains("spring.mail.username is blank"));
    }

    @Test
    void logsConfigurationErrorWhenMailEnabledButSenderUnavailable(CapturedOutput output) {
        @SuppressWarnings("unchecked")
        ObjectProvider<JavaMailSender> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(null);

        new MailService(provider, true, "sender@example.com");

        org.junit.jupiter.api.Assertions.assertTrue(output.getOut().contains("JavaMailSender is unavailable"));
    }

    @Test
    void skipsSendWhenRecipientBlank() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        @SuppressWarnings("unchecked")
        ObjectProvider<JavaMailSender> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(mailSender);

        MailService mailService = new MailService(provider, true, "sender@example.com");

        mailService.sendMail("   ", "subject", "body");

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendsMailWhenEnabledAndConfigured() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        @SuppressWarnings("unchecked")
        ObjectProvider<JavaMailSender> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(mailSender);

        MailService mailService = new MailService(provider, true, "sender@example.com");

        mailService.sendMail("user@example.com", "subject", "body");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}

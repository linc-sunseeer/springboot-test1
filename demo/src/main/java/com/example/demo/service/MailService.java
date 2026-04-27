package com.example.demo.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final boolean mailEnabled;
    private final String fromAddress;

    public MailService(ObjectProvider<JavaMailSender> mailSenderProvider,
                       @Value("${app.mail.enabled:false}") boolean mailEnabled,
                       @Value("${spring.mail.username:}") String fromAddress) {
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.mailEnabled = mailEnabled;
        this.fromAddress = fromAddress;

        if (mailEnabled && !StringUtils.hasText(fromAddress)) {
            log.error("Mail is enabled but spring.mail.username is blank. SMTP credentials or .env settings are missing.");
        }

        if (mailEnabled && this.mailSender == null) {
            log.error("Mail is enabled but JavaMailSender is unavailable. Check spring.mail configuration and dependencies.");
        }
    }

    @Async
    public void sendMail(String to, String subject, String body) {
        if (!mailEnabled) {
            log.warn("Mail skipped because app.mail.enabled is false. to={}, subject={}", to, subject);
            return;
        }

        if (mailSender == null) {
            log.error("Mail skipped because JavaMailSender is unavailable. to={}, subject={}", to, subject);
            return;
        }

        if (!StringUtils.hasText(to)) {
            log.warn("Mail skipped because recipient is blank. subject={}", subject);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        if (StringUtils.hasText(fromAddress)) {
            message.setFrom(fromAddress);
        }
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        try {
            log.info("Sending mail. to={}, subject={}", to, subject);
            mailSender.send(message);
            log.info("Mail sent successfully. to={}, subject={}", to, subject);
        } catch (MailException ex) {
            log.error("Mail send failed. to={}, subject={}, error={}", to, subject, ex.getMessage(), ex);
            // Do not re-throw: email failure must not abort business operations
            // (group buy confirmation/cancellation sends multiple emails in a loop)
        }
    }
}

package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.EmailService;
import com.digicompass.backend.application.security.EmailValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendResetLink(String toEmail, String token) {
        try {
            // Validate inputs
            if (toEmail == null || toEmail.isBlank() || !EmailValidator.isValid(toEmail)) {
                throw new IllegalArgumentException("Recipient email address cannot be null, blank, or invalid.");
            }
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("Reset token cannot be null or blank.");
            }

            // Build email content
            String resetUrl = "http://localhost:5173/reset-password?token=" + token;
            String subject = "Password Reset Request";
            String body = "Hello,\n\n" +
                    "You requested to reset your password. Click the link below to reset it:\n" +
                    resetUrl + "\n\n" +
                    "This link will expire in 15 minutes.\n\n" +
                    "If you didn’t request this, please ignore this email.";

            // Create and send message
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            log.info("Password reset email successfully sent to {0}", toEmail);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid input when sending reset link: {0}", e.getMessage());
            throw e;

        } catch (MailException e) {
            log.error(
                    String.format("Failed to send email to %s due to mail server error: %s", toEmail, e.getMessage()),
                    e);
            throw new RuntimeException("Unable to send password reset email at this time.", e);

        } catch (Exception e) {
            log.error(
                    String.format("Unexpected error occurred while sending reset link to %s: %s", toEmail, e.getMessage()),
                    e);
            throw new RuntimeException("An unexpected error occurred while sending email.", e);
        }
    }
}

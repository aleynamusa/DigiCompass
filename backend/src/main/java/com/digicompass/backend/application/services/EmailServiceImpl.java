package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetLink(String toEmail, String token) {
        String resetUrl = "http://localhost:5173/reset-password?token=" + token;

        String subject = "Password Reset Request";
        String body = "Hello,\n\n" +
                "You requested to reset your password. Click the link below to reset it:\n" +
                resetUrl + "\n\n" +
                "This link will expire in 15 minutes.\n\n" +
                "If you didn’t request this, please ignore this email.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}

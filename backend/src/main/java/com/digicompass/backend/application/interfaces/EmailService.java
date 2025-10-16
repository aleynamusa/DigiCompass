package com.digicompass.backend.application.interfaces;

public interface EmailService {
    void sendResetLink(String toEmail, String token);
}

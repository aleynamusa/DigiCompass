package com.digicompass.backend.infrastructure.interfaces;


public interface EmailClient {
    void sendResetLink(String toEmail, String token);
}

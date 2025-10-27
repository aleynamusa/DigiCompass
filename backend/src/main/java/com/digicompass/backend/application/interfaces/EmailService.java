package com.digicompass.backend.application.interfaces;


import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendResetLink(String toEmail, String token);
}

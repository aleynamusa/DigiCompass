package com.digicompass.backend.unit.interfaces;


import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendResetLink(String toEmail, String token);
}

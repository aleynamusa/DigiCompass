package com.digicompass.backend.unit.interfaces;

import org.springframework.stereotype.Service;

@Service
public interface PasswordResetService {
    void createPasswordResetToken(String email);
    void resetPassword(String token, String newPassword);
}

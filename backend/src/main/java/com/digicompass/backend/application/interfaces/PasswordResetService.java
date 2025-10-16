package com.digicompass.backend.application.interfaces;

import com.digicompass.backend.application.services.helpers.PasswordHasher;
import com.digicompass.backend.domain.models.User;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public interface PasswordResetService {
    void createPasswordResetToken(String email);
    void resetPassword(String token, String newPassword);
}

package com.digicompass.backend.repository.cache.interfaces;

import java.util.Optional;

public interface PasswordResetTokenRepository {
    void saveToken(String token, String email);
    Optional<String> getEmailByToken(String token);
    void deleteToken(String token);
}


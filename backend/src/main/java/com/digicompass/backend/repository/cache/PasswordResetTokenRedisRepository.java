package com.digicompass.backend.repository.cache;

import com.digicompass.backend.repository.cache.interfaces.PasswordResetTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class PasswordResetTokenRedisRepository implements PasswordResetTokenRepository {

    private final StringRedisTemplate redisTemplate;

    public PasswordResetTokenRedisRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveToken(String token, String email) {
        try {
            redisTemplate.opsForValue().set(token, email, 15, TimeUnit.MINUTES);
            log.info("[REPOSITORY] Password reset token saved for email: {}", email);
        } catch (Exception e) {
            log.error("[REPOSITORY] Redis error saving password reset token: {}", e.getMessage());
            throw new RuntimeException("Failed to save password reset token", e);
        }
    }

    @Override
    public Optional<String> getEmailByToken(String token) {
        try {
            String email = redisTemplate.opsForValue().get(token);
            if (email != null) {
                log.info("[REPOSITORY] Found email for token");
                return Optional.of(email);
            }
            log.info("[REPOSITORY] No email found for token (expired or invalid)");
            return Optional.empty();
        } catch (Exception e) {
            log.error("[REPOSITORY] Redis error getting email by token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void deleteToken(String token) {
        try {
            redisTemplate.delete(token);
            log.info("[REPOSITORY] Password reset token deleted");
        } catch (Exception e) {
            log.error("[REPOSITORY] Redis error deleting token: {}", e.getMessage());
            throw new RuntimeException("Failed to delete password reset token", e);
        }
    }
}


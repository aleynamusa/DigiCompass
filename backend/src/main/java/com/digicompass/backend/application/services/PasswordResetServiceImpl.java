package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.EmailService;
import com.digicompass.backend.application.interfaces.PasswordResetService;
import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.application.services.helpers.PasswordHasher;
import com.digicompass.backend.application.models.User;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {


    private final UserJpaRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;
    private final UserMapper userMapper;

    public PasswordResetServiceImpl(
            UserJpaRepository userRepository,
            StringRedisTemplate redisTemplate,
            EmailService emailService,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.emailService = emailService;
        this.userMapper = userMapper;
    }

    @Override
    public void createPasswordResetToken(String email) {
        try {
            if (email == null || email.isBlank()) {
                log.warn("[SERVICE] Email cannot be null or blank when creating reset token.");
                throw new IllegalArgumentException("Email cannot be null or blank.");
            }

            User user = userMapper.toDomain(userRepository.findByEmail(email));
            if (user == null) {
                log.warn("[SERVICE] No user found for email: {}", email);
                throw new IllegalArgumentException("User not found.");
            }

            String token = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(token, email, 15, TimeUnit.MINUTES);
            log.info("[SERVICE] Password reset token stored in Redis for user: {}", email);

            emailService.sendResetLink(email, token);
            log.info("[SERVICE] Password reset email sent successfully to: {}", email);

        } catch (IllegalArgumentException e) {
            log.warn("[SERVICE] Validation error while creating reset token: {}", e.getMessage());
            throw e;

        } catch (RedisConnectionFailureException e) {
            log.error("[SERVICE] Redis connection error while creating reset token for {}: {}",
                    email, e.getMessage());
            throw new RuntimeException("Failed to connect to Redis service.", e);

        } catch (DataAccessException e) {
            log.error("[SERVICE] Database access error while creating reset token for {}: {}",
                    email, e.getMessage());
            throw new RuntimeException("Database error while creating password reset token.", e);

        } catch (Exception e) {
            log.error("[SERVICE] Unexpected error creating password reset token for {}: {}",
                    email, e.getMessage());
            throw new RuntimeException("Unexpected error creating password reset token.", e);
        }
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        try {
            if (token == null || token.isBlank()) {
                log.warn("[SERVICE] Token cannot be null or blank during password reset.");
                throw new IllegalArgumentException("Token cannot be null or blank.");
            }

            if (newPassword == null || newPassword.isBlank()) {
                log.warn("[SERVICE] New password cannot be null or blank.");
                throw new IllegalArgumentException("New password cannot be null or blank.");
            }

            String email = redisTemplate.opsForValue().get(token);
            if (email == null) {
                log.warn("[SERVICE] Invalid or expired token used for password reset.");
                throw new IllegalArgumentException("Invalid or expired token.");
            }

            User user = userMapper.toDomain(userRepository.findByEmail(email));
            if (user == null) {
                log.warn("[SERVICE] User not found for token associated with email: {}", email);
                throw new IllegalArgumentException("User not found.");
            }

            user.setPassword(PasswordHasher.hash(newPassword));
            userRepository.save(userMapper.toEntity(user));
            log.info("[SERVICE] Password updated successfully for user: {}", email);

            redisTemplate.delete(token);
            log.info("[SERVICE] Password reset token deleted from Redis for user: {}", email);

        } catch (IllegalArgumentException e) {
            log.warn("[SERVICE] Validation error during password reset: {}", e.getMessage());
            throw e;

        } catch (RedisConnectionFailureException e) {
            log.error("[SERVICE] Redis connection failure during password reset for token {}: {}",
                    token, e.getMessage());
            throw new RuntimeException("Redis connection error during password reset.", e);

        } catch (DataAccessException e) {
            log.error("[SERVICE] Database access error during password reset for token {}: {}",
                    token, e.getMessage());
            throw new RuntimeException("Database error during password reset.", e);

        } catch (Exception e) {
            log.error("[SERVICE] Unexpected error during password reset for token {}: {}",
                    token, e.getMessage());
            throw new RuntimeException("Unexpected error during password reset.", e);
        }
    }
}

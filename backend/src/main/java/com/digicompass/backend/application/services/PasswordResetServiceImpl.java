package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.EmailService;
import com.digicompass.backend.application.interfaces.PasswordResetService;
import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.application.services.helpers.PasswordHasher;
import com.digicompass.backend.application.models.User;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.UserInterface;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final Logger LOGGER = Logger.getLogger(PasswordResetServiceImpl.class.getName());

    private final UserInterface userRepository;
    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;
    private final UserMapper userMapper;

    public PasswordResetServiceImpl(
            UserInterface userRepository,
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
                LOGGER.log(Level.WARNING, "Email cannot be null or blank when creating reset token.");
                throw new IllegalArgumentException("Email cannot be null or blank.");
            }

            User user = userMapper.toDomain(userRepository.findByEmail(email));
            if (user == null) {
                LOGGER.log(Level.WARNING, "No user found for email: {0}", email);
                throw new IllegalArgumentException("User not found.");
            }

            String token = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(token, email, 15, TimeUnit.MINUTES);
            LOGGER.log(Level.INFO, "Password reset token stored in Redis for user: {0}", email);

            emailService.sendResetLink(email, token);
            LOGGER.log(Level.INFO, "Password reset email sent successfully to: {0}", email);

        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validation error while creating reset token: {0}", e.getMessage());
            throw e;

        } catch (RedisConnectionFailureException e) {
            LOGGER.log(Level.SEVERE, "Redis connection error while creating reset token for {0}: {1}",
                    new Object[]{email, e.getMessage()});
            throw new RuntimeException("Failed to connect to Redis service.", e);

        } catch (DataAccessException e) {
            LOGGER.log(Level.SEVERE, "Database access error while creating reset token for {0}: {1}",
                    new Object[]{email, e.getMessage()});
            throw new RuntimeException("Database error while creating password reset token.", e);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error creating password reset token for {0}: {1}",
                    new Object[]{email, e.getMessage()});
            throw new RuntimeException("Unexpected error creating password reset token.", e);
        }
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        try {
            if (token == null || token.isBlank()) {
                LOGGER.log(Level.WARNING, "Token cannot be null or blank during password reset.");
                throw new IllegalArgumentException("Token cannot be null or blank.");
            }

            if (newPassword == null || newPassword.isBlank()) {
                LOGGER.log(Level.WARNING, "New password cannot be null or blank.");
                throw new IllegalArgumentException("New password cannot be null or blank.");
            }

            String email = redisTemplate.opsForValue().get(token);
            if (email == null) {
                LOGGER.log(Level.WARNING, "Invalid or expired token used for password reset.");
                throw new IllegalArgumentException("Invalid or expired token.");
            }

            User user = userMapper.toDomain(userRepository.findByEmail(email));
            if (user == null) {
                LOGGER.log(Level.WARNING, "User not found for token associated with email: {0}", email);
                throw new IllegalArgumentException("User not found.");
            }

            user.setPassword(PasswordHasher.hash(newPassword));
            userRepository.save(userMapper.toEntity(user));
            LOGGER.log(Level.INFO, "Password updated successfully for user: {0}", email);

            redisTemplate.delete(token);
            LOGGER.log(Level.INFO, "Password reset token deleted from Redis for user: {0}", email);

        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validation error during password reset: {0}", e.getMessage());
            throw e;

        } catch (RedisConnectionFailureException e) {
            LOGGER.log(Level.SEVERE, "Redis connection failure during password reset for token {0}: {1}",
                    new Object[]{token, e.getMessage()});
            throw new RuntimeException("Redis connection error during password reset.", e);

        } catch (DataAccessException e) {
            LOGGER.log(Level.SEVERE, "Database access error during password reset for token {0}: {1}",
                    new Object[]{token, e.getMessage()});
            throw new RuntimeException("Database error during password reset.", e);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during password reset for token {0}: {1}",
                    new Object[]{token, e.getMessage()});
            throw new RuntimeException("Unexpected error during password reset.", e);
        }
    }
}

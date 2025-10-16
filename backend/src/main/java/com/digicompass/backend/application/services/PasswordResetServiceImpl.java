package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.EmailService;
import com.digicompass.backend.application.interfaces.PasswordResetService;
import com.digicompass.backend.application.services.helpers.PasswordHasher;
import com.digicompass.backend.domain.models.User;
import com.digicompass.backend.infrastucture.persistence.repository.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserInterface userRepository;
    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;

    public PasswordResetServiceImpl(UserInterface userRepository,
                                StringRedisTemplate redisTemplate,
                                    EmailService emailService
                                ) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.emailService = emailService;
    }

    public void createPasswordResetToken(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        String token = UUID.randomUUID().toString();

        // Save token in Redis for 15 minutes
        redisTemplate.opsForValue().set(token, email, 15, TimeUnit.MINUTES);

        // Send email
        emailService.sendResetLink(email, token);
    }

    public void resetPassword(String token, String newPassword) {
        String email = redisTemplate.opsForValue().get(token);
        if (email == null) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        user.setPassword(PasswordHasher.hash(newPassword));
        userRepository.save(user);

        // Remove token after use
        redisTemplate.delete(token);
    }
}


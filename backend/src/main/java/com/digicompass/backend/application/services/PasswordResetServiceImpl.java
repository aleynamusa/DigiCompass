package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.EmailService;
import com.digicompass.backend.application.interfaces.PasswordResetService;
import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.application.services.helpers.PasswordHasher;
import com.digicompass.backend.domain.models.User;
import com.digicompass.backend.infrastucture.persistence.entity.UserEntity;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.UserInterface;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserInterface userRepository;
    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;
    private final UserMapper userMapper;

    public PasswordResetServiceImpl(UserInterface userRepository,
                                    StringRedisTemplate redisTemplate,
                                    EmailService emailService, UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.emailService = emailService;
        this.userMapper = userMapper;
    }

    public void createPasswordResetToken(String email) {
        User user = userMapper.toDomain(userRepository.findByEmail(email));

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        String token = UUID.randomUUID().toString();

        //save token in Redis for 15 minutes
        redisTemplate.opsForValue().set(token, email, 15, TimeUnit.MINUTES);

        //sends email
        emailService.sendResetLink(email, token);
    }

    public void resetPassword(String token, String newPassword) {
        String email = redisTemplate.opsForValue().get(token);
        if (email == null) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        User user = userMapper.toDomain(userRepository.findByEmail(email));

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        user.setPassword(PasswordHasher.hash(newPassword));
        userRepository.save(userMapper.toEntity(user)); //TODO take this to the db PLS

        //delete token after use
        redisTemplate.delete(token);
    }
}


package com.digicompass.backend.application.services;

import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.application.security.EmailValidator;
import com.digicompass.backend.application.security.PasswordValidator;
import com.digicompass.backend.application.services.helpers.PasswordHasher;
import com.digicompass.backend.application.interfaces.AuthService;
import com.digicompass.backend.domain.entity.UserEntity;
import com.digicompass.backend.application.models.User;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.UserInterface;
import jakarta.validation.Valid;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Validated
public class AuthServiceImpl implements AuthService {

    private final UserInterface userRepository;
    private final UserMapper userMapper;

    private static final Logger LOGGER = Logger.getLogger(AuthServiceImpl.class.getName());

    public AuthServiceImpl(UserInterface userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User signUp(@Valid User user) {
        try {
            if (user == null) {
                LOGGER.log(Level.WARNING, "User cannot be null when registering.");
                throw new IllegalArgumentException("User cannot be null.");
            }

            if (user.CalculateAge()  < 14) {
                LOGGER.log(Level.WARNING, "User cannot be less than 14 years old.");
                throw new IllegalArgumentException("User cannot be less than 14 years old.");
            }

            String pwError = PasswordValidator.getValidationError(user.getPassword());
            if (pwError != null) {
                LOGGER.log(Level.WARNING, "Password validation failed: {0}", pwError);
                throw new IllegalArgumentException(pwError);
            }

            String emailError = EmailValidator.getValidationError(user.getEmail());
            if (emailError != null) {
                LOGGER.log(Level.WARNING, "Email validation failed: {0}", emailError);
                throw new IllegalArgumentException(emailError);
            }

            LOGGER.log(Level.INFO, "Starting sign-up process for username: {0}", user.getUsername());

            // Hash the password securely
            String hashedPw = PasswordHasher.hash(user.getPassword());
            user.setPassword(hashedPw);
            LOGGER.log(Level.FINE, "Password hashed successfully for username: {0}", user.getUsername());

            // Save to the database
            UserEntity savedUser = userRepository.save(userMapper.toEntity(user));
            LOGGER.log(Level.INFO, "User signed up successfully with id: {0}", savedUser.getId());

            return userMapper.toDomain(savedUser);

        } catch (IllegalArgumentException e) {
            // Validation failure → rethrow directly
            LOGGER.log(Level.WARNING, "Validation error during sign-up: {0}", e.getMessage());
            throw e;

        } catch (DataAccessException e) {
            // Database-related problem
            LOGGER.log(Level.SEVERE, "Database access error during sign-up for {0}: {1}",
                    new Object[]{user != null ? user.getUsername() : "unknown", e.getMessage()});
            throw new RuntimeException("Database error while creating user account.", e);

        } catch (Exception e) {
            // Unexpected runtime issues (e.g. hashing failure)
            LOGGER.log(Level.SEVERE, "Unexpected error during sign-up for {0}: {1}",
                    new Object[]{user != null ? user.getUsername() : "unknown", e.getMessage()});
            throw new RuntimeException("Unexpected error during sign-up process.", e);
        }
    }

    @Override
    public User logIn(String username, String password) {
        try {
            LOGGER.log(Level.INFO, "Login attempt for username: {0}", username);

            if (username == null || username.isBlank() || password == null || password.isBlank()) {
                LOGGER.log(Level.WARNING, "Invalid login parameters (empty username or password).");
                throw new IllegalArgumentException("Username and password must not be blank.");
            }

            UserEntity userEntity = userRepository.findByUsername(username);
            if (userEntity == null) {
                LOGGER.log(Level.WARNING, "Login failed: user not found for username: {0}", username);
                return null;
            }

            boolean verified = PasswordHasher.verify(userEntity.getPassword(), password);
            if (verified) {
                LOGGER.log(Level.INFO, "Login successful for username: {0}", username);
                return userMapper.toDomain(userEntity);
            } else {
                LOGGER.log(Level.WARNING, "Login failed: invalid password for username: {0}", username);
                return null;
            }

        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validation error during login: {0}", e.getMessage());
            throw e;

        } catch (DataAccessException e) {
            LOGGER.log(Level.SEVERE, "Database access error during login for {0}: {1}",
                    new Object[]{username, e.getMessage()});
            throw new RuntimeException("Database error while processing login.", e);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during login for {0}: {1}",
                    new Object[]{username, e.getMessage()});
            throw new RuntimeException("Unexpected error while logging in.", e);
        }
    }
}

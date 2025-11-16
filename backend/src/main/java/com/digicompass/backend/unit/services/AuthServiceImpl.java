package com.digicompass.backend.unit.services;

import com.digicompass.backend.unit.mapper.UserMapper;
import com.digicompass.backend.unit.security.EmailValidator;
import com.digicompass.backend.unit.security.JWTToken;
import com.digicompass.backend.unit.security.PasswordValidator;
import com.digicompass.backend.unit.services.helpers.PasswordHasher;
import com.digicompass.backend.unit.interfaces.AuthService;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.unit.models.User;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserJpaRepository userRepository;
    private final UserMapper userMapper;
    private final JWTToken jwt;

    private static final Logger LOGGER = Logger.getLogger(AuthServiceImpl.class.getName());

    public AuthServiceImpl(UserJpaRepository userRepository, UserMapper userMapper, JWTToken jwt) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwt = jwt;
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


            String hashedPw = PasswordHasher.hash(user.getPassword());
            user.setPassword(hashedPw);
            LOGGER.log(Level.FINE, "Password hashed successfully for username: {0}", user.getUsername());

            UserEntity savedUser = userRepository.save(userMapper.toEntity(user));
            LOGGER.log(Level.INFO, "User signed up successfully with id: {0}", savedUser.getId());

            return userMapper.toDomain(savedUser);

        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validation error during sign-up: {0}", e.getMessage());
            throw e;

        } catch (DataAccessException e) {
            LOGGER.log(Level.SEVERE, "Database access error during sign-up for {0}: {1}",
                    new Object[]{user != null ? user.getUsername() : "unknown", e.getMessage()});
            throw new RuntimeException("Database error while creating user account.", e);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during sign-up for {0}: {1}",
                    new Object[]{user != null ? user.getUsername() : "unknown", e.getMessage()});
            throw new RuntimeException("Unexpected error during sign-up process.", e);
        }
    }

    @Override
    public Map<String, String> logIn(String username, String password) {
        try {
            LOGGER.log(Level.INFO, "Login attempt for username: {0}", username);

            if (username == null || username.isBlank() || password == null || password.isBlank()) {
                LOGGER.log(Level.WARNING, "Invalid login parameters (empty username or password).");
                throw new IllegalArgumentException("Username and password must not be blank.");
            }

            UserEntity userEntity = userRepository.findUserDocumentByUsername(username);
            if (userEntity == null) {
                LOGGER.log(Level.WARNING, "Login failed: user not found for username: {0}", username);
                return null;
            }

            boolean verified = PasswordHasher.verify(userEntity.getPassword(), password);
            if (verified) {
                LOGGER.log(Level.INFO, "Login successful for username: {0}", username);
                String accessToken = jwt.generateAccessToken(userMapper.toDomain(userEntity));
                String refreshToken = jwt.generateRefreshToken(userMapper.toDomain(userEntity));
                Map<String, String> tokens = Map.of(
                        "accessToken", accessToken,
                        "refreshToken", refreshToken
                );
                return tokens;
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

    public Map<String, String> refresh(Map<String, String> tokens) {
        try {
            String refreshToken = tokens.get("refreshToken");

            if (jwt.isTokenExpired(refreshToken) || !jwt.isRefreshToken(refreshToken)) {
                throw new IllegalArgumentException("Refresh token expired or not valid.");
            }

            Claims claims = jwt.extractAllClaims(refreshToken);
            User user = new User();
            user.setId(claims.get("id", Long.class));
            user.setUsername(claims.getSubject());

            String newAccessToken = jwt.generateAccessToken(user);
            String newRefreshToken = jwt.generateRefreshToken(user);

            Map<String, String> newTokens = Map.of(
                    "accessToken", newAccessToken,
                    "refreshToken", newRefreshToken
            );

            return newTokens;
        }catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validation error during refresh token: {0}", e.getMessage());
            throw e;
        }catch(Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during refresh token.", e);
            throw new RuntimeException("Unexpected error during refresh token.", e);
        }

    }
}

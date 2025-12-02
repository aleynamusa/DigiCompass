package com.digicompass.backend.application.services;

import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.application.security.EmailValidator;
import com.digicompass.backend.application.security.JWTToken;
import com.digicompass.backend.application.security.PasswordValidator;
import com.digicompass.backend.application.services.helpers.PasswordHasher;
import com.digicompass.backend.application.interfaces.AuthService;
import com.digicompass.backend.repository.entity.RoleEntity;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.application.models.User;
import com.digicompass.backend.repository.repositories.RoleJpaRepository;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final UserMapper userMapper;
    private final JWTToken jwt;


    public AuthServiceImpl(UserJpaRepository userRepository, RoleJpaRepository roleRepository, UserMapper userMapper, JWTToken jwt) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.jwt = jwt;
    }

    @Override
    public User signUp(@Valid User user) {
        try {
            if (user == null) {
                throw new IllegalArgumentException("User cannot be null.");
            }

            if (user.CalculateAge() < 14) {
                throw new IllegalArgumentException("User cannot be less than 14 years old.");
            }

            String pwError = PasswordValidator.getValidationError(user.getPassword());
            if (pwError != null) throw new IllegalArgumentException(pwError);

            String emailError = EmailValidator.getValidationError(user.getEmail());
            if (emailError != null) throw new IllegalArgumentException(emailError);

            log.info("[SERVICE] Starting sign-up process for username: {}", user.getUsername());

            user.setPassword(PasswordHasher.hash(user.getPassword()));

            RoleEntity role = roleRepository.findByRole("user");
            if (role == null) {
                throw new IllegalStateException("Default role 'user' not found in database");
            }

            UserEntity entity = userMapper.toEntity(user);
            entity.setRole(role);

            UserEntity savedUser = userRepository.save(entity);

            log.info("[SERVICE] User signed up successfully with id: {}", savedUser.getId());
            return userMapper.toDomain(savedUser);

        } catch (Exception e) {
            log.error("[SERVICE] Error during sign-up: {}",e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public Map<String, String> logIn(String username, String password) {
        log.info("Login attempt for username: {}", username);

        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {
            log.warn("[SERVICE] Invalid login parameters");
            throw new IllegalArgumentException("Username and password must not be blank.");
        }

        try {
            UserEntity userEntity = userRepository.findUserDocumentByUsername(username);

            if (userEntity == null) {
                log.warn("[SERVICE] User not found for username: {}", username);
                return null;
            }

            boolean verified = PasswordHasher.verify(userEntity.getPassword(), password);
            if (!verified) {
                log.warn("[SERVICE] Invalid password for username: {}", username);
                return null;
            }

            log.info("[SERVICE] Login successful for username: {}", username);

            String accessToken = jwt.generateAccessToken(userMapper.toDomain(userEntity));
            String refreshToken = jwt.generateRefreshToken(userMapper.toDomain(userEntity));

            return Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken
            );

        } catch (DataAccessException e) {
            log.error("[SERVICE] Database error during login: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("[SERVICE] Unexpected error during login: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected login error", e);
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

            log.info("[SERVICE] New access token: {}", newAccessToken);
            log.info("[SERVICE] New refresh token: {}", newRefreshToken);

            return newTokens;
        }catch (IllegalArgumentException e) {
            log.warn("[SERVICE] Validation error during refresh token: {}", e.getMessage());
            throw e;
        }catch(Exception e) {
            log.error("[SERVICE] Unexpected error during refresh token.", e);
            throw new RuntimeException("Unexpected error during refresh token.", e);
        }

    }
}

package com.digicompass.backend.application.services;

import com.digicompass.backend.infrastructure.interfaces.EmailClient;
import com.digicompass.backend.repository.cache.interfaces.PasswordResetTokenRepository;
import com.digicompass.backend.repository.repositories.UserJpaRepository;

import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.application.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private EmailClient emailService;

    @Mock
    private UserMapper userMapperMock;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    private UserEntity userEntity;
    private User user;

    @BeforeEach
    void setUp() {

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("oldPassword");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("oldPassword");
    }

    @Test
    void testCreatePasswordResetToken_Success() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(userMapperMock.toDomain(userEntity)).thenReturn(user);

        doNothing().when(tokenRepository).saveToken(anyString(), eq(email));

        passwordResetService.createPasswordResetToken(email);

        verify(emailService, times(1)).sendResetLink(eq(email), anyString());
    }


    @Test
    void testCreatePasswordResetToken_UserNotFound() {
        String email = "missing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);
        when(userMapperMock.toDomain((UserEntity) null)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.createPasswordResetToken(email)
        );

        verify(emailService, never()).sendResetLink(anyString(), anyString());
    }

    @Test
    void testResetPassword_Success() {
        String token = UUID.randomUUID().toString();
        String email = "user@example.com";
        String newPassword = "NewPass123@";

        when(tokenRepository.getEmailByToken(token))
                .thenReturn(Optional.of(email));

        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(userMapperMock.toDomain(userEntity)).thenReturn(user);
        when(userMapperMock.toEntity(user)).thenReturn(userEntity);

        passwordResetService.resetPassword(token, newPassword);

        verify(userRepository).save(any());
        verify(tokenRepository).deleteToken(token);

        assertTrue(user.getPassword().startsWith("$argon2"));
    }


    @Test
    void testResetPassword_InvalidToken() {
        String badToken = "bad-token";

        when(tokenRepository.getEmailByToken(badToken))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.resetPassword(badToken, "Password123@")
        );

        verify(userRepository, never()).save(any());
    }


    @Test
    void testResetPassword_UserNotFound() {
        String token = "valid-token";
        String email = "ghost@example.com";

        when(tokenRepository.getEmailByToken(token))
                .thenReturn(Optional.of(email));

        when(userRepository.findByEmail(email)).thenReturn(null);
        when(userMapperMock.toDomain((UserEntity) null)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.resetPassword(token, "Password123@")
        );

        verify(userRepository, never()).save(any());
    }


    @Test
    void testCreatePasswordResetToken_RedisConnectionFailure() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(userMapperMock.toDomain(userEntity)).thenReturn(user);

        doThrow(new RuntimeException("Failed to connect to Redis"))
                .when(tokenRepository).saveToken(anyString(), anyString());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> passwordResetService.createPasswordResetToken(email)
        );

        assertTrue(ex.getMessage().contains("Unexpected error"));
        verify(emailService, never()).sendResetLink(anyString(), anyString());
    }


    @Test
    void testCreatePasswordResetToken_DatabaseError() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email))
                .thenThrow(new org.springframework.dao.DataAccessResourceFailureException("DB error"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> passwordResetService.createPasswordResetToken(email)
        );

        assertTrue(ex.getMessage().contains("Database error"));
        verify(emailService, never()).sendResetLink(anyString(), anyString());
    }

    @Test
    void testCreatePasswordResetToken_UnexpectedError() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email))
                .thenThrow(new RuntimeException("Something broke"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> passwordResetService.createPasswordResetToken(email)
        );

        assertTrue(ex.getMessage().contains("Unexpected error"));
    }


    @Test
    void testResetPassword_RedisConnectionFailure() {
        String token = "reset-token";

        when(tokenRepository.getEmailByToken(token))
                .thenThrow(new RuntimeException("Redis down"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> passwordResetService.resetPassword(token, "NewPass123@")
        );

        assertTrue(ex.getMessage().contains("Unexpected error"));
    }



    @Test
    void testResetPassword_DatabaseError() {
        String token = "reset-token";
        String email = "test@example.com";

        when(tokenRepository.getEmailByToken(token))
                .thenReturn(Optional.of(email));

        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(userMapperMock.toDomain(userEntity)).thenReturn(user);
        when(userMapperMock.toEntity(user)).thenReturn(userEntity);

        when(userRepository.save(any()))
                .thenThrow(new DataAccessResourceFailureException("DB failure"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> passwordResetService.resetPassword(token, "NewPass123@")
        );

        assertTrue(ex.getMessage().contains("Database error"));
    }



    @Test
    void testResetPassword_UnexpectedError() {
        String token = "reset-token";
        String email = "test@example.com";

        when(tokenRepository.getEmailByToken(token))
                .thenReturn(Optional.of(email));

        when(userRepository.findByEmail(email))
                .thenThrow(new RuntimeException("Unknown failure"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> passwordResetService.resetPassword(token, "NewPass123@")
        );

        assertTrue(ex.getMessage().contains("Unexpected error"));
    }


    @Test
    void createResetPasswordToken_ShouldThrowIllegalArgumentException_WhenEmailIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            passwordResetService.createPasswordResetToken(null);
        });
    }

    @Test
    void resetResetPasswordToken_ShouldThrowIllegalArgumentException_WhenEmailIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> {
            passwordResetService.createPasswordResetToken("");
        });
    }

    @Test
    void resetPassword_ShouldThrowIllegalArgumentException_WhenNewTokenIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            passwordResetService.resetPassword(null, "newPassword1@");
        });
    }

    @Test
    void resetPassword_ShouldThrowIllegalArgumentException_WhenNewTokenIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> {
            passwordResetService.resetPassword("", "newPassword1@");
        });
    }

    @Test
    void resetPassword_ShouldThrowIllegalArgumentException_WhenPasswordIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            passwordResetService.resetPassword("valid-token", null);
        });
    }

    @Test
    void resetPassword_ShouldThrowIllegalArgumentException_WhenNewPasswordIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> {
            passwordResetService.resetPassword("valid-token", "");
        });
    }

    @Test
    void resetPassword_ShouldThrowIllegalArgumentException_WhenPasswordIsTooWeak() {
        assertThrows(IllegalArgumentException.class, () -> {
            passwordResetService.resetPassword("valid-token", "aaaa");
        });
    }


}

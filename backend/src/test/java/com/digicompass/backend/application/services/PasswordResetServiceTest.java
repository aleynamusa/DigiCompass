package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.EmailService;
import com.digicompass.backend.domain.models.User;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.UserInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserInterface userRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps); //lenient not using in all of the tests
    }

    @Test
    void testCreatePasswordResetToken_Success() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(user);
        passwordResetService.createPasswordResetToken(email);
        verify(emailService, times(1)) .sendResetLink(eq(email), anyString());
    }

    @Test
    void testCreatePasswordResetToken_UserNotFound() {
        // Arrange
        String email = "missing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.createPasswordResetToken(email)
        );

        // Verify that no email is sent
        verify(emailService, never()).sendResetLink(anyString(), anyString());

    }

    @Test
    void testResetPassword_Success() {
        String token = UUID.randomUUID().toString();
        String email = "user@example.com";
        String newPassword = "newPass123";

        User user = new User();
        user.setEmail(email);
        user.setPassword("oldPass");

        // Redis returns email for the token
        when(valueOps.get(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(user);

        passwordResetService.resetPassword(token, newPassword);

        verify(userRepository, times(1)).save(user);
        verify(redisTemplate, times(1)).delete(token);
        assertNotEquals(newPassword, user.getPassword()); // password was hashed
    }

    @Test
    void testResetPassword_InvalidToken() {
        String badToken = "bad-token";
        when(valueOps.get(badToken)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.resetPassword(badToken, "password123")
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void testResetPassword_UserNotFound() {
        String token = "valid-token";
        String email = "ghost@example.com";

        when(valueOps.get(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.resetPassword(token, "password123")
        );

        verify(redisTemplate, never()).delete(anyString());
    }
}

package com.digicompass.backend.application.services;

import com.digicompass.backend.repository.repositories.UserJpaRepository;
import com.digicompass.backend.application.interfaces.EmailService;
import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.application.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Mock
    private EmailService emailService;

    @Mock
    private UserMapper userMapperMock;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    private UserEntity userEntity;
    private User user;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);//lenient is used when we want to use it when neccessary so dont have problem when it is not called from one of the methods

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

        passwordResetService.createPasswordResetToken(email);

        verify(valueOps, times(1)).set(anyString(), eq(email), eq(15L), eq(java.util.concurrent.TimeUnit.MINUTES));
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
        verify(valueOps, never()).set(any(), any(), anyLong(), any());
    }

    @Test
    void testResetPassword_Success() {
        String token = UUID.randomUUID().toString();
        String email = "user@example.com";
        String newPassword = "newPass123@";

        UserEntity entity = new UserEntity();
        entity.setEmail(email);
        entity.setPassword("oldPassword");

        User domainUser = new User();
        domainUser.setEmail(email);
        domainUser.setPassword("oldPassword");

        when(valueOps.get(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(entity);
        when(userMapperMock.toDomain(entity)).thenReturn(domainUser);
        when(userMapperMock.toEntity(domainUser)).thenReturn(entity);

        passwordResetService.resetPassword(token, newPassword);

        verify(userRepository, times(1)).save(entity);
        verify(redisTemplate, times(1)).delete(token);

        assertNotEquals(newPassword, domainUser.getPassword(), "Password should be hashed");
        assertTrue(domainUser.getPassword().startsWith("$argon2"), "Password should be Argon2 hash");
    }

    @Test
    void testResetPassword_InvalidToken() {
        String badToken = "bad-token";
        when(valueOps.get(badToken)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.resetPassword(badToken, "password123")
        );

        verify(userRepository, never()).save(any());
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    void testResetPassword_UserNotFound() {
        String token = "valid-token";
        String email = "ghost@example.com";

        when(valueOps.get(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(null);
        when(userMapperMock.toDomain((UserEntity) null)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.resetPassword(token, "password123")
        );

        verify(redisTemplate, never()).delete(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreatePasswordResetToken_RedisConnectionFailure() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(userMapperMock.toDomain(userEntity)).thenReturn(user);

        doThrow(new RedisConnectionFailureException("Redis down"))
                .when(valueOps)
                .set(anyString(), eq(email), anyLong(), any());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> passwordResetService.createPasswordResetToken(email)
        );

        assertTrue(ex.getMessage().contains("Failed to connect to Redis"));
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
        String email = "test@example.com";

        when(valueOps.get(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(userMapperMock.toDomain(userEntity)).thenReturn(user);

        doThrow(new RedisConnectionFailureException("Redis offline"))
                .when(redisTemplate)
                .delete(token);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> passwordResetService.resetPassword(token, "newPass123@")
        );

        assertTrue(ex.getMessage().contains("Redis connection error"));
    }


    @Test
    void testResetPassword_DatabaseError() {
        String token = "reset-token";
        String email = "test@example.com";

        when(valueOps.get(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(userMapperMock.toDomain(userEntity)).thenReturn(user);

        when(userRepository.save(any()))
                .thenThrow(new org.springframework.dao.DataAccessResourceFailureException("DB failure"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> passwordResetService.resetPassword(token, "newPass123@")
        );

        assertTrue(ex.getMessage().contains("Database error"));
    }


    @Test
    void testResetPassword_UnexpectedError() {
        String token = "reset-token";
        String email = "test@example.com";

        when(valueOps.get(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenThrow(new RuntimeException("Unknown failure"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> passwordResetService.resetPassword(token, "newPass123")
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

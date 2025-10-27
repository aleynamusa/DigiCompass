package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.EmailService;
import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.domain.entity.UserEntity;
import com.digicompass.backend.infrastucture.persistence.models.User;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.UserInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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

        // Arrange
        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        when(userMapperMock.toDomain(userEntity)).thenReturn(user);

        // Act
        passwordResetService.createPasswordResetToken(email);

        // Assert
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
        String newPassword = "newPass123";

        // Arrange
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

        // Act
        passwordResetService.resetPassword(token, newPassword);

        // Assert
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
}

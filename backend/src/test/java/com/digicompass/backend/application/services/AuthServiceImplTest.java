package com.digicompass.backend.application.services;

import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.application.models.User;
import com.digicompass.backend.application.security.JWTToken;
import com.digicompass.backend.application.services.helpers.PasswordHasher;
import com.digicompass.backend.repository.entity.RoleEntity;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.RoleJpaRepository;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit") // tag test for test planning, to differentiate them
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserJpaRepository repoMock;

    @Mock
    private RoleJpaRepository roleMock;

    @Mock
    private UserMapper userMapperMock;

    @Mock
    private JWTToken jwtMock;

    @InjectMocks
    private AuthServiceImpl service;

    private User user;
    private UserEntity userEntity;

    @BeforeEach
    void init() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("PlainPass12@");
        user.setEmail("test@gmail.com");
        user.setRoleId(2L);
        user.setBirthDate(LocalDate.of(1997, 10, 3));

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("testuser");
        userEntity.setRole(new RoleEntity(2L, "user"));
        userEntity.setBirthDate(LocalDate.of(1997, 10, 3));
        userEntity.setPassword("hashedPass");
    }

   //SIGN UP TESTS

    @Test
    void signUp_ShouldHashPassword_AndSaveUser() {
        when(roleMock.findByRole("user")).thenReturn(new RoleEntity(2L, "user"));

        when(userMapperMock.toEntity(user)).thenReturn(userEntity);
        when(repoMock.save(userEntity)).thenReturn(userEntity);
        when(userMapperMock.toDomain(userEntity)).thenReturn(user);

        try (MockedStatic<PasswordHasher> mocked = mockStatic(PasswordHasher.class)) {
            mocked.when(() -> PasswordHasher.hash(any()))
                    .thenReturn("hashedPw123");

            User result = service.signUp(user);

            assertNotNull(result);
            verify(repoMock).save(any(UserEntity.class));
            assertEquals("hashedPw123", user.getPassword());
        }
    }


    @Test
    void signUp_ShouldThrow_WhenUserIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.signUp(null)
        );
        assertEquals("User cannot be null.", ex.getMessage());
    }

    @Test
    void signUp_ShouldThrow_WhenUserUnder14() {
        user.setBirthDate(LocalDate.now().minusYears(10));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.signUp(user)
        );
        assertEquals("User cannot be less than 14 years old.", ex.getMessage());
    }

    @Test
    void signUp_ShouldThrow_WhenPasswordInvalid() {
        user.setPassword("weak");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.signUp(user)
        );

        assertTrue(ex.getMessage().contains("Password"));
    }

    @Test
    void signUp_ShouldThrow_WhenEmailInvalid() {
        user.setEmail("wrong.com");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.signUp(user)
        );

        assertTrue(ex.getMessage().contains("Email"));
    }

    //LOGIN TESTS

    @Test
    void logIn_ShouldThrow_WhenUsernameBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> service.logIn(" ", "password"));
    }

    @Test
    void logIn_ShouldThrow_WhenPasswordNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.logIn("testuser", null));
    }

    @Test
    void logIn_ShouldReturnNull_WhenUserNotFound() {
        when(repoMock.findUserDocumentByUsername("missing")).thenReturn(null);

        Map<String, String> result = service.logIn("missing", "pwd");

        assertNull(result);
    }

    @Test
    void logIn_ShouldReturnTokens_WhenPasswordCorrect() {
        when(repoMock.findUserDocumentByUsername("testuser"))
                .thenReturn(userEntity);

        when(userMapperMock.toDomain(any(UserEntity.class))).thenReturn(user);

        when(jwtMock.generateAccessToken(any(User.class))).thenReturn("access123");
        when(jwtMock.generateRefreshToken(any(User.class))).thenReturn("refresh123");

        try (MockedStatic<PasswordHasher> mocked = mockStatic(PasswordHasher.class)) {
            mocked.when(() -> PasswordHasher.verify("hashedPass", "PlainPass12@"))
                    .thenReturn(true);

            Map<String, String> tokens = service.logIn("testuser", "PlainPass12@");

            assertNotNull(tokens);
            assertEquals("access123", tokens.get("accessToken"));
            assertEquals("refresh123", tokens.get("refreshToken"));

            verify(jwtMock).generateAccessToken(any(User.class));
            verify(jwtMock).generateRefreshToken(any(User.class));
        }
    }


    @Test
    void logIn_ShouldReturnNull_WhenPasswordWrong() {
        when(repoMock.findUserDocumentByUsername("testuser"))
                .thenReturn(userEntity);

        try (MockedStatic<PasswordHasher> mocked = mockStatic(PasswordHasher.class)) {
            mocked.when(() -> PasswordHasher.verify("hashedPass", "wrong"))
                    .thenReturn(false);

            Map<String, String> result = service.logIn("testuser", "wrong");

            assertNull(result);
            verify(jwtMock, never()).generateAccessToken(any());
        }
    }
}

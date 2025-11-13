package com.digicompass.backend.application.services;

import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.application.security.JWTToken;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.application.models.User;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private UserJpaRepository repoMock;
    @Mock
    private UserMapper userMapperMock;

    @Mock
    private JWTToken jwtMock;

    @InjectMocks
    private AuthServiceImpl service;

    private User user;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("plainPassword12@");
        user.setBirthDate(LocalDate.of(1997, 10, 03));
        user.setEmail("test@gmail.com");

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("testuser");
        userEntity.setPassword("hashedPassword");
    }

    @Test
    void signUp_ShouldHashPassword_AndSaveUser() {
        //arrange
        when(userMapperMock.toEntity(any(User.class))).thenReturn(userEntity);
        when(repoMock.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapperMock.toDomain(any(UserEntity.class))).thenReturn(user);

        //act
        User result = service.signUp(user);

        //assert
        assertNotNull(result);
        verify(repoMock, times(1)).save(any(UserEntity.class));
        verify(userMapperMock, times(1)).toEntity(any(User.class));
        verify(userMapperMock, times(1)).toDomain(any(UserEntity.class));

        assertNotEquals("plainPassword", user.getPassword());
    }

    @Test
    void signUp_ShouldThrowException_WhenPasswordIsNotValidated() {

        User invalidUser = new User(1L, "testuser", "test@gmail.com", LocalDate.of(1997, 10, 03), "plainPassword");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.signUp(invalidUser));

        assertTrue(
                ex.getMessage().contains("Password"),
                "Expected password validation error but got: " + ex.getMessage()
        );
    }

    @Test
    void signUp_ShouldThrowException_WhenEmailIsNotValid() {

        User invalidUser = new User(1L, "testuser", "testgm.com", LocalDate.of(1997, 10, 03), "plainPassword12@");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.signUp(invalidUser));

        assertTrue(
                ex.getMessage().contains("Email"),
                "Expected email validation error but got: " + ex.getMessage()
        );
    }

    @Test
    void signUp_ShouldThrowException_WhenUserIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.signUp(null));

        assertEquals("User cannot be null.", ex.getMessage());
    }

    @Test
    void signUp_ShouldThrowException_WhenUserIsUnder14() {
        user.setBirthDate(LocalDate.of(2015, 10, 03));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.signUp(user));

        assertEquals("User cannot be less than 14 years old.", ex.getMessage());
    }

//    @Test
//    void testLogIn_ShouldReturnNull_UserNotFound() {
//        when(repoMock.findByUsername("unknownUser")).thenReturn(null);
//
//        String result = service.logIn("unknownUser", "anyPassword");
//
//        assertNull(result);
//        verify(repoMock).findByUsername("unknownUser");
//        verifyNoMoreInteractions(repoMock, userMapperMock);
//    }

//    @Test
//    void logIn_ShouldReturnToken_WhenPasswordMatches() {
//
//
//
//        when(repoMock.findByUsername("testuser")).thenReturn(userEntity);
//        when(jwtMock.generateToken(userMapperMock.toDomain(userEntity)).thenReturn("mocked-jwt-token");
//
//        try (MockedStatic<PasswordHasher> mockedHasher = mockStatic(PasswordHasher.class)) {
//            mockedHasher.when(() -> PasswordHasher.verify("hashedPassword", "correctPassword"))
//                    .thenReturn(true);
//
//            String result = service.logIn("testuser", "correctPassword");
//
//            assertNotNull(result);
//            assertEquals("mocked-jwt-token", result);
//            verify(repoMock).findByUsername("testuser");
//            verify(jwtMock).generateToken("testuser");
//            verifyNoInteractions(userMapperMock);
//        }
//    }

//    @Test
//    void logIn_ShouldReturnNull_WhenPasswordIsIncorrect() {
//        when(repoMock.findByUsername("testuser")).thenReturn(userEntity);
//
//        try (MockedStatic<PasswordHasher> mockedHasher = mockStatic(PasswordHasher.class)) {
//            mockedHasher.when(() -> PasswordHasher.verify("hashedPassword", "wrongPassword"))
//                    .thenReturn(false);
//
//            String result = service.logIn("testuser", "wrongPassword");
//
//            assertNull(result);
//            verify(repoMock).findByUsername("testuser");
//            verifyNoInteractions(userMapperMock, jwtMock);
//        }
//    }

    @Test
    void logIn_ShouldThrowException_NullPassword() {
            assertThrows(IllegalArgumentException.class, () -> service.logIn("testuser", null));

    }

    @Test
    void shouldThrowException_WhenUsernameIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> service.logIn(" ", "somePassword"));
    }


}
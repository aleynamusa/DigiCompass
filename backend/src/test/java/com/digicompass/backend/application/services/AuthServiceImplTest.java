package com.digicompass.backend.application.services;

import com.digicompass.backend.application.services.helpers.PasswordHasher;
import com.digicompass.backend.domain.models.User;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl service;  // Changed from AuthService to AuthServiceImpl

    @Mock
    private UserRepository repoMock;

    @Test
    void signUp() {
        User user = new User(null, "aleyna", "aleyna@gmail.com", (byte)19, "helloooo");

        when(repoMock.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId("123");
            return u;
        });

        try (MockedStatic<PasswordHasher> mockedHasher = mockStatic(PasswordHasher.class)) {
            mockedHasher.when(() -> PasswordHasher.hash("helloooo")).thenReturn("hashedPassword");

            User savedUser = service.signUp(user);

            assertNotNull(savedUser.getId());
            assertEquals("aleyna", savedUser.getUsername());
            assertEquals("aleyna@gmail.com", savedUser.getEmail());
            assertEquals("hashedPassword", savedUser.getPassword());
        }
    }

    @Test
    void signUp_NullUser_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.signUp(null);
        });
    }

    @Test
    void dummyTest() {
        assertTrue(true);
    }

    @Test
    void testLogIn_Success() {
        User user = new User("123", "aleyna", "aleyna@gmail.com", (byte)19, PasswordHasher.hash("helloooo"));

        when(repoMock.findByUsername("aleyna")).thenReturn(user);

        boolean loggedIn = service.logIn("aleyna", "helloooo");

        assertTrue(loggedIn);
    }

    @Test
    void testLogIn_WrongPassword(){
        User user = new User("123", "aleyna", "aleyna@gmail.com", (byte)19, PasswordHasher.hash("helloooo"));

        when(repoMock.findByUsername("aleyna")).thenReturn(user);

        boolean loggedIn = service.logIn("aleyna", "helloo");

        assertFalse(loggedIn);
    }

    @Test
    void testLogIn_UserNotFound() {
        when(repoMock.findByUsername("unknownUser")).thenReturn(null);

        boolean loggedIn = service.logIn("unknownUser", "anyPassword");

        assertFalse(loggedIn);
    }
}
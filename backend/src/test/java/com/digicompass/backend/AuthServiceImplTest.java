package com.digicompass.backend;

import com.digicompass.backend.application.services.AuthServiceImpl;
import com.digicompass.backend.application.services.helpers.PasswordHasher;
import com.digicompass.backend.domain.models.User;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

//the test runs but it keeps compiling - fixed
class AuthServiceImplTest {
    //mock object setup
    @InjectMocks
    private AuthServiceImpl service;

    @Mock
    UserRepository repoMock;

    // @Before - before the other methods
    @BeforeEach
    public void setUp()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signUp() {
        User user = new User(null, "aleyna", "aleyna@gmail.com", (byte)19, "helloooo");

        // Mock repository save
        when(repoMock.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId("123");
            return u;
        });

        // Mock static PasswordHasher
        try (MockedStatic<PasswordHasher> mockedHasher = mockStatic(PasswordHasher.class)) {
            mockedHasher.when(() -> PasswordHasher.hash("helloooo")).thenReturn("hashedPassword");

            User savedUser = service.signUp(user);

            assertNotNull(savedUser.getId());
            assertEquals("aleyna", savedUser.getUsername());
            assertEquals("aleyna@gmail.com", savedUser.getEmail());
            assertEquals("hashedPassword", savedUser.getPassword());
        }
    }
}
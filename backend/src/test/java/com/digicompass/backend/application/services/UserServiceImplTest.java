package com.digicompass.backend.application.services;

import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.application.models.User;
import com.digicompass.backend.repository.entity.RoleEntity;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        user = new User(
                1L,
                "testUser",
                "test@mail.com",
                LocalDate.of(2000, 1, 1),
                "passWord56@",
                1L
        );

        userEntity = new UserEntity(
                1L,
                "testUser",
                "test@mail.com",
                LocalDate.of(2000, 1, 1),
                "passWord56@",
                new RoleEntity(1L, "ADMIN")
        );
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(userEntity));
        when(userMapper.toDomain(anyList())).thenReturn(List.of(user));

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        verify(userRepository).findAll();
        verify(userMapper).toDomain(anyList());
    }


    @Test
    void testGetUserById_Found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDomain(userEntity)).thenReturn(user);

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        verify(userRepository).findById(1L);
        verify(userMapper).toDomain(userEntity);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getUserById(1L));

        assertEquals("User not found with id: 1", ex.getMessage());
        verify(userRepository).findById(1L);
        verify(userMapper, never()).toDomain(any(UserEntity.class));
    }

    @Test
    void testDeleteUser_Success() {
        when(userMapper.toEntity(user)).thenReturn(userEntity);

        userService.deleteUser(user);

        verify(userRepository).delete(userEntity);
    }

    @Test
    void testDeleteUser_Null_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUser(null));
        assertEquals("User cannot be null", ex.getMessage());
        verify(userRepository, never()).delete(any());
    }

    @Test
    void testCheckUsernameAvailability_Exists() {
        when(userRepository.findAllUsernames()).thenReturn(List.of("john", "testUser"));

        boolean result = userService.checkUsernameAvailability("testUser");

        assertTrue(result);
        verify(userRepository).findAllUsernames();
    }

    @Test
    void testCheckUsernameAvailability_NotExists() {
        when(userRepository.findAllUsernames()).thenReturn(List.of("john"));

        boolean result = userService.checkUsernameAvailability("missing");

        assertFalse(result);
        verify(userRepository).findAllUsernames();
    }

    @Test
    void testCheckUsernameAvailability_Null_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.checkUsernameAvailability(null));
        verify(userRepository, never()).findAllUsernames();
    }

    @Test
    void testCheckEmailAvailability_Exists() {
        when(userRepository.findAllEmails()).thenReturn(List.of("test@mail.com"));

        boolean result = userService.checkEmailAvailability("test@mail.com");

        assertTrue(result);
        verify(userRepository).findAllEmails();
    }

    @Test
    void testCheckEmailAvailability_NotExists() {
        when(userRepository.findAllEmails()).thenReturn(List.of("no@mail.com"));

        boolean result = userService.checkEmailAvailability("missing@mail.com");

        assertFalse(result);
        verify(userRepository).findAllEmails();
    }

    @Test
    void testCheckEmailAvailability_Null_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.checkEmailAvailability(null));
        verify(userRepository, never()).findAllEmails();
    }

}
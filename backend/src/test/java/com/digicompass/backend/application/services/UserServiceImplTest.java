package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.RatingService;
import com.digicompass.backend.infrastructure.interfaces.S3;
import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.application.models.route.Route;
import com.digicompass.backend.application.models.User;
import com.digicompass.backend.repository.entity.RoleEntity;
import com.digicompass.backend.repository.entity.RouteEntity;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RouteMapper routeMapper;

    @Mock
    private RatingService ratingService;

    @Mock
    private RouteJpaRepository routeRepository;

    @Mock
    private S3 s3Service;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserEntity userEntity;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        user = new User(
                1L,
                "testUser",
                "test@mail.com",
                LocalDate.of(2000, 1, 1),
                "passWord56@",
                1L, null, null, true
        );

        userEntity = new UserEntity(
                1L,
                "testUser",
                "test@mail.com",
                LocalDate.of(2000, 1, 1),
                "passWord56@",
                new RoleEntity(1L, "ADMIN"), null, null, true
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
        when(userRepository.existsByUsername("testUser")).thenReturn(true);

        boolean result = userService.checkUsernameAvailability("testUser");

        assertFalse(result);
        verify(userRepository).existsByUsername("testUser");
    }

    @Test
    void testCheckUsernameAvailability_NotExists() {
        when(userRepository.existsByUsername("missing")).thenReturn(false);

        boolean result = userService.checkUsernameAvailability("missing");

        assertTrue(result);
        verify(userRepository).existsByUsername("missing");
    }

    @Test
    void testCheckUsernameAvailability_ThrowsException() {
        Mockito.when(userRepository.existsByUsername("john"))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () ->
                userService.checkUsernameAvailability("john")
        );
    }

    @Test
    void testCheckUsernameAvailability_Null_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.checkUsernameAvailability(null));
        verify(userRepository, never()).findAllUsernames();
    }

    @Test
    void testCheckEmailAvailability_Exists() {
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);

        boolean result = userService.checkEmailAvailability("test@mail.com");

        assertFalse(result);
        verify(userRepository).existsByEmail("test@mail.com");
    }

    @Test
    void testCheckEmailAvailability_NotExists() {
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);

        boolean result = userService.checkEmailAvailability("test@mail.com");

        assertTrue(result);
        verify(userRepository).existsByEmail("test@mail.com");
    }

    @Test
    void testCheckEmailAvailability_Null_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.checkEmailAvailability(null));
        verify(userRepository, never()).findAllEmails();
    }

    @Test
    void testCheckEmailAvailability_ThrowsException() {
        Mockito.when(userRepository.existsByEmail("test@gmail.com"))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () ->
                userService.checkEmailAvailability("test@gmail.com")
        );
    }

    @Test
    void getByUsername_returnsUsersAndPresignsImages() {
        String keyword = "john";

        UserEntity e1 = new UserEntity();
        e1.setImageUrl("path/to/img");

        User u1 = new User();
        u1.setImageUrl("path/to/img");

        when(userRepository.findByUsernameContainingIgnoreCase(keyword))
                .thenReturn(List.of(e1));

        when(userMapper.toDomain(List.of(e1)))
                .thenReturn(List.of(u1));

        when(s3Service.getPreSignedUrl("path/to/img"))
                .thenReturn("signed-url");

        List<User> result = userService.getByUsername(keyword);

        assertEquals(1, result.size());
        assertEquals("signed-url", result.get(0).getImageUrl());
    }

    @Test
    void getByUsername_throwsResponseStatusExceptionOnError() {
        String keyword = "abc";

        when(userRepository.findByUsernameContainingIgnoreCase(keyword))
                .thenThrow(new RuntimeException("DB failed"));

        assertThrows(ResponseStatusException.class,
                () -> userService.getByUsername(keyword));
    }

    @Test
    void updateProfileVisibility_updatesVisibility() {

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDomain(userEntity)).thenReturn(user);
        when(userMapper.toEntity(user)).thenReturn(userEntity);

        userService.updateProfileVisibility(userId, true);

        assertTrue(user.isPublicProfile());
        verify(userRepository).save(userEntity);
    }

    @Test
    void updateProfileVisibility_throwsResponseStatusExceptionWhenUserNotFound() {

        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> userService.updateProfileVisibility(99L, true));
    }


    @Test
    void updateBio_updatesBioField() {

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDomain(userEntity)).thenReturn(user);
        when(userMapper.toEntity(user)).thenReturn(userEntity);

        userService.updateBio(userId, "New bio");

        assertEquals("New bio", user.getBio());
        verify(userRepository).save(userEntity);
    }

    @Test
    void updateBio_throwsRuntimeExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.updateBio(10L, "bio"));
    }

    @Test
    void uploadProfilePicture_success() throws Exception {
        MultipartFile file = mock(MultipartFile.class);

        UserEntity entity = new UserEntity();
        entity.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(s3Service.uploadImage(anyLong(), eq(file))).thenReturn("img-key");

        userService.uploadProfilePicture(1L, file);

        assertEquals("img-key", entity.getImageUrl());
        verify(userRepository).save(entity);
    }

    @Test
    void uploadProfilePicture_userNotFound_throwsNoSuchElement() throws Exception {
        MultipartFile file = mock(MultipartFile.class);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                userService.uploadProfilePicture(1L, file)
        );

        verify(s3Service, never()).uploadImage(anyString(), any());
    }

    @Test
    void uploadProfilePicture_saveFails_triggersRollback() throws Exception {
        MultipartFile file = mock(MultipartFile.class);

        UserEntity entity = new UserEntity();
        entity.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(s3Service.uploadImage(anyLong(), eq(file))).thenReturn("img-key");

        doThrow(new RuntimeException("DB error"))
                .when(userRepository).save(entity);

        assertThrows(RuntimeException.class,
                () -> userService.uploadProfilePicture(1L, file));

        verify(s3Service).rollbackS3Upload("img-key");
    }


    @Test
    void uploadProfilePicture_s3UploadFails_throwsIOException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);

        UserEntity entity = new UserEntity();
        entity.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(s3Service.uploadImage(anyLong(), eq(file)))
                .thenThrow(new IOException("IO failed"));

        assertThrows(IOException.class,
                () -> userService.uploadProfilePicture(1L, file));

        verify(userRepository, never()).save(any());
    }


    @Test
    void getRoutesById_userNotFound_throwsNoSuchElement() {
        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> userService.getRoutesById(5L));

        verify(routeRepository, never()).findAllByUserId(anyLong());
    }

    @Test
    void getRoutesById_success() {
        when(userRepository.findById(5L)).thenReturn(Optional.of(new UserEntity()));

        RouteEntity r1 = new RouteEntity();
        when(routeRepository.findAllByUserId(5L)).thenReturn(List.of(r1));

        Route mapped = new Route();
        when(routeMapper.toDomain(List.of(r1))).thenReturn(List.of(mapped));

        List<Route> result = userService.getRoutesById(5L);

        assertEquals(1, result.size());
    }

}
package com.digicompass.backend.application.services;

import com.digicompass.backend.application.mapper.FavouriteRouteMapper;
import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.application.models.FavouriteRoute;
import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.application.models.User;
import com.digicompass.backend.repository.entity.FavouriteRouteEntity;
import com.digicompass.backend.repository.entity.RouteEntity;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.FavouriteRouteJpaRepository;
import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserActionsServiceImplTest {
    @Mock
    private FavouriteRouteJpaRepository favouriteRouteRepository;

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private RouteJpaRepository routeRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RouteMapper routeMapper;

    @Mock
    private FavouriteRouteMapper favouriteRouteMapper;

    @InjectMocks
    private UserActionsServiceImpl userActionsService;

    private UserEntity mockUser;
    private RouteEntity mockRoute;
    private User user;
    private Route route;

    @BeforeEach
    void setup() {

        mockUser = new UserEntity();
        mockUser.setId(1L);

        user = new User();
        user.setId(1L);

        mockRoute = new RouteEntity();
        mockRoute.setId(1L);

        route = new Route();
        route.setId(1L);
    }


    @Test
    void testFavouriteRoute_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(routeRepository.findById(1L)).thenReturn(Optional.of(mockRoute));

        when(userMapper.toDomain(mockUser)).thenReturn(user);
        when(routeMapper.toDomain(mockRoute)).thenReturn(route);

        FavouriteRouteEntity expectedEntity = new FavouriteRouteEntity();
        when(favouriteRouteMapper.toEntity(any(FavouriteRoute.class)))
                .thenReturn(expectedEntity);

        userActionsService.favouriteRoute(1L, 1L);

        verify(favouriteRouteRepository, times(1)).save(expectedEntity);
    }


    @Test
    void testUnfavouriteRoute_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(routeRepository.findById(1L)).thenReturn(Optional.of(mockRoute));

        FavouriteRoute domain = new FavouriteRoute(null, null, LocalDateTime.now());
        when(userMapper.toDomain(mockUser)).thenReturn(domain.getUser());
        when(routeMapper.toDomain(mockRoute)).thenReturn(domain.getRoute());

        FavouriteRouteEntity mappedEntity = new FavouriteRouteEntity();
        when(favouriteRouteMapper.toEntity(any(FavouriteRoute.class))).thenReturn(mappedEntity);

        userActionsService.unfavouriteRoute(1L, 1L);

        verify(favouriteRouteRepository, times(1)).delete(mappedEntity);
    }

    @Test
    void testFavouriteRoute_userOrRouteNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userActionsService.favouriteRoute(1L, 1L)
        );

        assertEquals("User does not exist.", exception.getMessage());
        verify(favouriteRouteRepository, never()).save(any());
    }

    @Test
    void testUnfavouriteRoute_userOrRouteNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userActionsService.unfavouriteRoute(1L, 1L)
        );

        assertEquals("User or route does not exist.", exception.getMessage());
        verify(favouriteRouteRepository, never()).delete(any());
    }

    @Test
    void testFavouriteRoute_repositoryThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(routeRepository.findById(1L)).thenReturn(Optional.of(mockRoute));

        when(favouriteRouteMapper.toEntity(any())).thenThrow(new RuntimeException("DB ERROR"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userActionsService.favouriteRoute(1L, 1L)
        );

        assertEquals("Error liking the route.", exception.getMessage());
    }

    @Test
    void testUnfavouriteRoute_repositoryThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(routeRepository.findById(1L)).thenReturn(Optional.of(mockRoute));

        when(favouriteRouteMapper.toEntity(any())).thenThrow(new RuntimeException("DB ERROR"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userActionsService.unfavouriteRoute(1L, 1L)
        );

        assertEquals("Error unliking the route.", exception.getMessage());
    }

    @Test
    void testIsLikedRoute_success_true() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(routeRepository.findById(1L)).thenReturn(Optional.of(mockRoute));
        when(favouriteRouteRepository.existsByIdUserIdAndIdRouteId(1L, 1L))
                .thenReturn(true);

        boolean result = userActionsService.isLikedRoute(1L, 1L);

        assertTrue(result);
        verify(favouriteRouteRepository, times(1))
                .existsByIdUserIdAndIdRouteId(1L, 1L);
    }

    @Test
    void testIsLikedRoute_success_false() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(routeRepository.findById(1L)).thenReturn(Optional.of(mockRoute));
        when(favouriteRouteRepository.existsByIdUserIdAndIdRouteId(1L, 1L))
                .thenReturn(false);

        boolean result = userActionsService.isLikedRoute(1L, 1L);

        assertFalse(result);
        verify(favouriteRouteRepository, times(1))
                .existsByIdUserIdAndIdRouteId(1L, 1L);
    }

    @Test
    void testIsLikedRoute_userOrRouteNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userActionsService.isLikedRoute(1L, 1L)
        );

        assertEquals("User does not exist.", exception.getMessage());
        verify(favouriteRouteRepository, never()).existsByIdUserIdAndIdRouteId(any(), any());
    }

    @Test
    void testIsLikedRoute_repositoryThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(routeRepository.findById(1L)).thenReturn(Optional.of(mockRoute));
        when(favouriteRouteRepository.existsByIdUserIdAndIdRouteId(1L, 1L))
                .thenThrow(new RuntimeException("DB ERROR"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userActionsService.isLikedRoute(1L, 1L)
        );

        assertEquals("Error checking liked state.", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("DB ERROR", exception.getCause().getMessage());
    }

}

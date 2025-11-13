package com.digicompass.backend.application.services;

import com.digicompass.backend.repository.repositories.RatingJpaRepository;
import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import com.digicompass.backend.application.mapper.RatingMapper;
import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.repository.entity.RatingEntity;
import com.digicompass.backend.application.models.Rating;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class RatingServiceImplTest {

    @Mock
    private RatingJpaRepository ratingRepo;

    @Mock
    private RouteJpaRepository routeRepo;

    @Mock
    private RatingMapper ratingMapper;

    @Mock
    private RouteMapper routeMapper;

    @InjectMocks
    private RatingServiceImpl ratingService;


    //getRouteRating TESTS
    @Test
    void testGetRouteRating_Success() {
        // Arrange
        Long id = 1L;
        when(routeRepo.getAllIds()).thenReturn(List.of(1L));
        when(ratingRepo.getAvgRatingByRoute(1L)).thenReturn(4.5);

        // Act
        Double rating = ratingService.getRouteRating(id);

        // Assert
        assertEquals(4.5, rating);
        verify(ratingRepo).getAvgRatingByRoute(1L);
    }

    @Test
    void testGetRouteRating_NullReturnedFromRepo() {
        Long routeId = 1L;

        when(routeRepo.getAllIds()).thenReturn(List.of(1L, 2L));
        when(ratingRepo.getRatingsByRoute(routeId)).thenReturn(null);

        Double result = ratingService.getRouteRating(routeId);

        assertEquals(0.0, result);
    }

    @Test
    void testGetRouteRating_InvalidId() {
        Long invalidId = 99L;
        when(routeRepo.getAllIds()).thenReturn(List.of(1L, 2L));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ratingService.getRouteRating(invalidId)
        );

        assertTrue(exception.getMessage().contains("could not be found"));
        verify(ratingRepo, never()).getRatingsByRoute(any());
    }

    @Test
    void testGetRouteRating_ThrowsExceptionInsideTryBlock() {
        Long routeId = 1L;

        when(routeRepo.getAllIds()).thenReturn(List.of(1L));
        when(ratingRepo.getAvgRatingByRoute(routeId))
                .thenThrow(new RuntimeException("DB failure"));

        ArithmeticException exception = assertThrows(
                ArithmeticException.class,
                () -> ratingService.getRouteRating(routeId)
        );

        assertTrue(exception.getMessage().contains("DB failure"));
    }

    //getRatingsByRouteId TESTS

    @Test
    void testGetRatingsByRouteId_Success() {
        Long routeId = 1L;
        RatingEntity entity1 = new RatingEntity();
        RatingEntity entity2 = new RatingEntity();
        Rating rating1 = new Rating();
        Rating rating2 = new Rating();

        when(routeRepo.getAllIds()).thenReturn(List.of(1L, 2L));
        when(ratingRepo.getRatingsByRoute(routeId)).thenReturn(Arrays.asList(entity1, entity2));
        when(ratingMapper.toDomain(Arrays.asList(entity1, entity2))).thenReturn(Arrays.asList(rating1, rating2));


        List<Rating> result = ratingService.getRatingsByRouteId(routeId);

        assertEquals(2, result.size());
        verify(ratingRepo, times(1)).getRatingsByRoute(routeId);
        verify(ratingMapper, times(1)).toDomain(anyList());
    }

    @Test
    void testGetRatingsByRouteId_InvalidId() {
        Long invalidId = 99L;

        when(routeRepo.getAllIds()).thenReturn(List.of(1L, 2L));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ratingService.getRatingsByRouteId(invalidId)
        );

        assertTrue(exception.getMessage().contains("could not be found"));
        verify(ratingRepo, never()).getRatingsByRoute(any());
    }

    @Test
    void testGetRatingsByRouteId_ThrowsExceptionInsideTryBlock() {
        Long routeId = 1L;

        when(routeRepo.getAllIds()).thenReturn(List.of(1L));
        when(ratingRepo.getRatingsByRoute(routeId)).thenThrow(new RuntimeException("Database failure"));

        ArithmeticException exception = assertThrows(
                ArithmeticException.class,
                () -> ratingService.getRatingsByRouteId(routeId)
        );

        assertTrue(exception.getMessage().contains("Database failure"));
    }

    @Test
    void testGetRatingsByRouteId_EmptyList() {
        Long routeId = 1L;

        when(routeRepo.getAllIds()).thenReturn(List.of(1L));
        when(ratingRepo.getRatingsByRoute(routeId)).thenReturn(Collections.emptyList());
        when(ratingMapper.toDomain(anyList())).thenReturn(Collections.emptyList());

        List<Rating> result = ratingService.getRatingsByRouteId(routeId);

        assertTrue(result.isEmpty());
        verify(ratingRepo, times(1)).getRatingsByRoute(routeId);
    }
}
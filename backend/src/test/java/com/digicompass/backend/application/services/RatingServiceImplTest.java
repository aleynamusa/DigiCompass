package com.digicompass.backend.application.services;

import com.digicompass.backend.repository.repositories.RatingJpaRepository;
import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import com.digicompass.backend.application.mapper.RatingMapper;
import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.repository.entity.RatingEntity;
import com.digicompass.backend.application.models.route.Rating;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
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
        when(ratingRepo.getAvgRatingByRoute(1L)).thenReturn(4.50);

        // Act
        String rating = ratingService.getRouteRating(id);

        // Assert
        assertEquals("4.50", rating);
        verify(ratingRepo).getAvgRatingByRoute(1L);
    }

    @Test
    void testGetRouteRating_NullReturnedFromRepo() {
        Long routeId = 1L;

        when(routeRepo.getAllIds()).thenReturn(List.of(1L, 2L));
        when(ratingRepo.getAvgRatingByRoute(routeId)).thenReturn(null);

        String result = ratingService.getRouteRating(routeId);

        assertEquals("0.00", result);
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

    //ADD Rating
    @Test
    void addRating_ReturnsRating_WhenSaveSuccessful() {
        Rating rating = new Rating();
        RatingEntity entity = new RatingEntity();
        entity.setId(1L);

        Rating mappedRating = new Rating();
        mappedRating.setId(1L);

        when(ratingMapper.toEntity(rating)).thenReturn(entity);
        when(ratingRepo.save(entity)).thenReturn(entity);
        when(ratingMapper.toDomain(entity)).thenReturn(mappedRating);

        Rating result = ratingService.addRating(rating);

        assertEquals(mappedRating, result);
        verify(ratingRepo).save(entity);
    }


    @Test
    void addRating_ReturnsNull_WhenSavedEntityHasNullId() {
        Rating rating = new Rating();
        RatingEntity entity = new RatingEntity();
        Rating mappedRating = new Rating();

        when(ratingMapper.toEntity(rating)).thenReturn(entity);
        when(ratingRepo.save(entity)).thenReturn(entity);
        when(ratingMapper.toDomain(entity)).thenReturn(mappedRating);

        Rating result = ratingService.addRating(rating);

        assertNull(result);
    }


    @Test
    void addRating_ThrowsNullPointerException_WhenMapperFails() {
        Rating rating = new Rating();

        assertThrows(NullPointerException.class, () -> ratingService.addRating(rating));
    }


    //DELETE rating test

    @Test
    void deleteRating_shouldDelete_whenIdExistsInRatingRepo() {
        Long ratingId = 1L;

        when(ratingRepo.existsById(ratingId)).thenReturn(true);

        ratingService.deleteRating(ratingId);

        verify(ratingRepo, times(1)).deleteById(ratingId);
    }

    @Test
    void deleteRating_shouldNotDelete_whenIdNotInRatingRepo() {
        Long ratingId = 1L;

        when(ratingRepo.existsById(ratingId)).thenReturn(false);
        ratingService.deleteRating(ratingId);

        verify(ratingRepo, never()).deleteById(any());
    }

    @Test
    void deleteRating_shouldThrow_whenRepoThrows() {
        Long ratingId = 1L;

        when(ratingRepo.existsById(ratingId)).thenReturn(true);
        doThrow(new RuntimeException("DB error"))
                .when(ratingRepo).deleteById(ratingId);

        ArithmeticException ex = assertThrows(
                ArithmeticException.class,
                () -> ratingService.deleteRating(ratingId)
        );

        assertTrue(ex.getMessage().contains("Unexpected error deleting Rating"));
    }

    //UPDAte rating Test

    @Test
    void updateRating_shouldUpdateSuccessfully() {
        Rating rating = new Rating();
        rating.setId(1L);

        RatingEntity entity = new RatingEntity();
        RatingEntity savedEntity = new RatingEntity();
        Rating mappedBack = new Rating();

        when(ratingMapper.toEntity(rating)).thenReturn(entity);
        when(ratingRepo.save(entity)).thenReturn(savedEntity);
        when(ratingMapper.toDomain(savedEntity)).thenReturn(mappedBack);

        Rating result = ratingService.updateRating(rating);

        assertEquals(mappedBack, result);
        verify(ratingRepo, times(1)).save(entity);
    }

    @Test
    void updateRating_shouldThrow_whenRepoFails() {
        Rating rating = new Rating();
        rating.setId(1L);

        when(ratingMapper.toEntity(rating)).thenThrow(new RuntimeException("DB Error"));

        ArithmeticException ex = assertThrows(
                ArithmeticException.class,
                () -> ratingService.updateRating(rating)
        );

        assertTrue(ex.getMessage().contains("Unexpected error updating Rating"));
    }


}
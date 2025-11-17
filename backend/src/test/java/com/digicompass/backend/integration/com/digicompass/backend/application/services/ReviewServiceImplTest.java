package com.digicompass.backend.application.services;

import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import com.digicompass.backend.application.mapper.ReviewMapper;
import com.digicompass.backend.repository.entity.ReviewEntity;
import com.digicompass.backend.application.models.Review;
import com.digicompass.backend.repository.repositories.ReviewJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
class ReviewServiceImplTest {

    @Mock
    ReviewJpaRepository reviewRepoMock;
    @Mock
    ReviewMapper reviewMapperMock;

    @Mock
    RouteJpaRepository routeRepoMock;

    @InjectMocks
    ReviewServiceImpl reviewServiceMock;

    private Long validRouteId;

    @BeforeEach
    void setUp() {
        validRouteId = 1L;
    }


    @Test
    void getReviewsByRoute_ThrowsException_WhenRouteIsNullOrZero()
    {
        assertThrows(IllegalArgumentException.class, () -> reviewServiceMock.getReviewsByRoute(null));
        assertThrows(IllegalArgumentException.class, () -> reviewServiceMock.getReviewsByRoute(0L));
        assertThrows(IllegalArgumentException.class, () -> reviewServiceMock.getReviewsByRoute(-9L));

    }

    @Test
    void getReviewsByRoute_ThrowsIllegalArgumentException_WhenRouteIdNotFound() {

        Long routeId = 9L;
        when(routeRepoMock.getAllIds()).thenReturn(List.of(1L, 2L, 3L));

        assertThrows(IllegalArgumentException.class, () -> reviewServiceMock.getReviewsByRoute(routeId));
    }

    @Test
    void getReviewsByRoute_ReturnsMappedReviews_WhenValidId() {
        List<Long> validIds = List.of(1L, 2L, 3L);
        when(routeRepoMock.getAllIds()).thenReturn(validIds);

        List<ReviewEntity> entityList = List.of(new ReviewEntity(), new ReviewEntity());
        when(reviewRepoMock.getReviewsByRoute(validRouteId)).thenReturn(entityList);

        var domainList = List.of(new Review(), new Review());
        when(reviewMapperMock.toDomain(entityList)).thenReturn(domainList);

        List<Review> result = reviewServiceMock.getReviewsByRoute(validRouteId);

        assertEquals(2, result.size());
        verify(reviewRepoMock).getReviewsByRoute(validRouteId);
        verify(reviewMapperMock).toDomain(entityList);
    }

    @Test
    void getReviewsByRoute_ReturnsEmptyList_WhenRepoReturnsNull() {
        when(routeRepoMock.getAllIds()).thenReturn(List.of(1L));
        when(reviewRepoMock.getReviewsByRoute(validRouteId)).thenReturn(null);

        List<Review> result = reviewServiceMock.getReviewsByRoute(validRouteId);

        assertTrue(result.isEmpty());
        verify(reviewMapperMock, never()).toDomain(List.of());
    }

    @Test
    void getReviewsByRoute_ThrowsRuntimeException_WhenRepoThrowsError() {
        when(routeRepoMock.getAllIds()).thenReturn(List.of(1L));
        when(reviewRepoMock.getReviewsByRoute(validRouteId)).thenThrow(new RuntimeException("DB failure"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reviewServiceMock.getReviewsByRoute(validRouteId)
        );

        assertTrue(exception.getMessage().contains("Unexpected error"));
        verify(reviewRepoMock).getReviewsByRoute(validRouteId);
    }

}
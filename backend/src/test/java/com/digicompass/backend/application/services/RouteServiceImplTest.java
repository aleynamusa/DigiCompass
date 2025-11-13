package com.digicompass.backend.application.services;

import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import com.digicompass.backend.application.interfaces.RatingService;
import com.digicompass.backend.application.interfaces.ReviewService;
import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.repository.entity.RouteEntity;
import com.digicompass.backend.application.models.Rating;
import com.digicompass.backend.application.models.Review;
import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.application.models.RouteGeometry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class RouteServiceImplTest {
    @Mock
    private RouteJpaRepository routeRepository;

    @Mock
    private RouteMapper routeMapper;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RatingService ratingService;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private RouteServiceImpl routeService;

    private Route route;
    private Long routeId;

    @BeforeEach
    void setUp() {
        routeId = 1L;
        route = new Route();
        route.setId(routeId);
        route.setName("Test Route");

        Geometry geometry = new GeometryFactory().createPoint();
        route.setRouteGeometry(geometry);
    }

    @Test
    void getRoutes_ReturnsListOfRoutesSuccessfully() {
        List<Route> mappedRoutes = List.of(route);
        when(routeMapper.toDomain(anyList())).thenReturn(mappedRoutes);
        when(routeRepository.findAll()).thenReturn(List.of());


        List<Route> result = routeService.getRoutes();

        assertEquals(1, result.size());
        verify(routeRepository).findAll();

    }

    @Test
    void getRoutes_ThrowsResponseStatusException_WhenRepositoryFails() {
        when(routeRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> routeService.getRoutes()
        );

        assertTrue(ex.getReason().contains("Failed to fetch routes"));
    }

    @Test
    void getRouteById_ReturnsRouteGeometrySuccessfully() throws Exception {
        when(routeRepository.findById(routeId)).thenReturn(Optional.of(new RouteEntity()));
        when(routeRepository.getAllIds()).thenReturn(List.of(1L, 2L, 3L));
        when(routeMapper.toDomain(new RouteEntity())).thenReturn(route);


        when(objectMapper.readTree(anyString())).thenReturn(null);

        RouteGeometry result = routeService.getRouteById(routeId);

        assertEquals(routeId, result.getId());
        verify(routeRepository).findById(routeId);

    }

    @Test
    void getRouteById_ThrowsNotFound_WhenRouteMissing() {
        when(routeRepository.getAllIds()).thenReturn(null);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> routeService.getRouteById(routeId)
        );

        assertEquals(404, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Route not found"));
    }

    @Test
    void getRouteById_ThrowsResponseStatusException_WhenJsonFails() throws Exception {
        when(routeRepository.findById(routeId)).thenReturn(Optional.of(new RouteEntity()));
        when(routeRepository.getAllIds()).thenReturn(List.of(routeId));
        when(routeMapper.toDomain(new RouteEntity())).thenReturn(route);

        when(reviewService.getReviewsByRoute(routeId)).thenReturn(List.of());
        when(ratingService.getRatingsByRouteId(routeId)).thenReturn(List.of());
        when(objectMapper.readTree(anyString())).thenThrow(new JsonProcessingException("Bad JSON") {});

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> routeService.getRouteById(routeId)
        );

        assertEquals(500, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Failed to convert route geometry"));
    }


}
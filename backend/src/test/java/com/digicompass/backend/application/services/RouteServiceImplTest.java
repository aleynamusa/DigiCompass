package com.digicompass.backend.application.services;

import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import com.digicompass.backend.application.interfaces.RatingService;
import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.repository.entity.RouteEntity;
import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.application.models.RouteGeometry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
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

@Tag("unit")
@ExtendWith(SpringExtension.class)
class RouteServiceImplTest {
    @Mock
    private RouteJpaRepository routeRepository;

    @Mock
    private RouteMapper routeMapper;

    @Mock
    private RatingService ratingService;


    @Mock
    private ObjectMapper objectMapper;

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
        when(routeRepository.findAll()).thenReturn(List.of(new RouteEntity()));
        when(routeMapper.toDomain(anyList())).thenReturn(List.of(route));
        when(ratingService.getRouteRating(anyLong())).thenReturn("4.50");

        List<Route> result = routeService.getRoutes();

        assertEquals(1, result.size());
        assertEquals(4.5f, Double.parseDouble(result.get(0).getAverageRating()), 0.0001);
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
    void getRoutes_ThrowsException_WhenSomethingUnexpectedOccurs() {

        when(routeRepository.findFiltered(anyString(), anyString(), anyFloat()))
                .thenThrow(new RuntimeException("DB failure"));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> routeService.getFilteredRoutes("hiking", "easy", 10f)
        );

        assertEquals(500, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Failed to filter routes"));
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
        when(routeRepository.findById(routeId))
                .thenReturn(Optional.of(new RouteEntity()));

        when(routeMapper.toDomain(any(RouteEntity.class)))
                .thenReturn(route);

        when(routeRepository.getAllIds())
                .thenReturn(List.of(routeId));

        when(objectMapper.readTree(anyString()))
                .thenThrow(new JsonProcessingException("Bad JSON") {});

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> routeService.getRouteById(routeId)
        );

        assertEquals(500, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Failed to convert route geometry"));
    }

    @Test
    void searchRoutes_ThrowsResponseStatusException_WhenRepositoryFails() {
        String keyword = "test";

        when(routeRepository.getRouteEntitiesByName(keyword))
                .thenThrow(new RuntimeException("DB failed"));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> routeService.searchRoutes(keyword)
        );

        assertEquals(500, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Failed to filter routes"));
    }



    @Test
    void searchRoutes_ReturnsMappedRoutes() {
        List<RouteEntity> mockEntities = List.of(new RouteEntity(), new RouteEntity());
        List<Route> mappedRoutes = List.of(new Route(), new Route());

        when(routeRepository.getRouteEntitiesByName("trail")).thenReturn(mockEntities);
        when(routeMapper.toDomain(mockEntities)).thenReturn(mappedRoutes);

        List<Route> result = routeService.searchRoutes("trail");

        assertEquals(2, result.size());
        assertNull(result.get(0).getRouteGeometry());
        assertNull(result.get(1).getRouteGeometry());

        verify(routeRepository).getRouteEntitiesByName("trail");
        verify(routeMapper).toDomain(mockEntities);
    }

    @Test
    void searchRoutes_ThrowsInternalServerError_WhenRepositoryFails() {

        when(routeRepository.getRouteEntitiesByName("trail"))
                .thenThrow(new RuntimeException("DB failure"));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> routeService.searchRoutes("trail")
        );

        assertEquals(500, ex.getStatusCode().value());
        assertEquals("Failed to filter routes.", ex.getReason());
    }

}
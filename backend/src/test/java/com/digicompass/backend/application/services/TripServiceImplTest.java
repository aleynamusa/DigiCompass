package com.digicompass.backend.application.services;

import com.digicompass.backend.application.mapper.TripMapper;
import com.digicompass.backend.application.models.Trip;
import com.digicompass.backend.repository.entity.TripEntity;
import com.digicompass.backend.repository.repositories.TripJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripServiceImplTest {

    @Mock
    private TripJpaRepository tripJpaRepository;

    @Mock
    private TripMapper tripMapper;

    @InjectMocks
    private TripServiceImpl tripService;

    @Test
    void createTrip_success_setsUserId_and_callsMapperAndRepository() {
        Trip trip = new Trip();
        trip.setName("TestTrip");
        Long userId = 42L;
        TripEntity mappedEntity = new TripEntity();

        when(tripMapper.ToEntity(trip)).thenReturn(mappedEntity);
        when(tripJpaRepository.save(mappedEntity)).thenReturn(mappedEntity);

        tripService.createTrip(trip, userId);

        assertEquals(userId, trip.getUserId(), "createTrip should set the userId on the trip");
        verify(tripMapper, times(1)).ToEntity(trip);
        verify(tripJpaRepository, times(1)).save(mappedEntity);
    }

    @Test
    void createTrip_repositoryThrows_isWrappedWithRuntimeExceptionContainingTripName() {
        Trip trip = new Trip();
        trip.setName("FailTrip");
        Long userId = 1L;
        TripEntity mappedEntity = new TripEntity();

        when(tripMapper.ToEntity(trip)).thenReturn(mappedEntity);
        when(tripJpaRepository.save(mappedEntity)).thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tripService.createTrip(trip, userId));
        assertTrue(ex.getMessage().contains("Error while creating trip"), "Exception message should indicate create error");
        assertTrue(ex.getMessage().contains(trip.getName()), "Exception message should include the trip name");
    }

    @Test
    void getAllRoutes_returnsEmptyList() {
        List<String> routes = tripService.getAllRoutes();

        assertNotNull(routes);
        assertTrue(routes.isEmpty(), "getAllRoutes should return an empty list by current implementation");
    }
}


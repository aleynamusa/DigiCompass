package com.digicompass.backend.application.interfaces;

import com.digicompass.backend.application.models.Trip;

import java.util.List;

public interface TripService {
    void createTrip(Trip trip,Long id);
    List<String> getAllRoutes();
}

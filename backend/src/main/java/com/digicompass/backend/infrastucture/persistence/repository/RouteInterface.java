package com.digicompass.backend.infrastucture.persistence.repository;

import com.digicompass.backend.domain.models.Route;

import java.util.List;

public interface RouteInterface {
    List<Route> getAllRoutes();
    Route getRouteByName(String name);
    void deleteRoute(Route route);
    Route createRoute(Route route);
}

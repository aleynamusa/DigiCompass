package com.digicompass.backend.infrastucture.persistence.repository.interfaces;

import com.digicompass.backend.domain.models.Route;
import com.digicompass.backend.infrastucture.persistence.entity.RouteEntity;

import java.util.List;

public interface RouteInterface {
    List<RouteEntity> getAllRoutes();
    RouteEntity getRouteByName(String name);
    void deleteRoute(RouteEntity route);
    RouteEntity createRoute(RouteEntity route);
    RouteEntity findById(long id);
    List<RouteEntity> getRoutesByKeyword(String keyword);
    List<RouteEntity> getAllRoutesByType(String type);
    List<RouteEntity> getAllRoutesByDifficulty(String difficulty);
    List<RouteEntity> getAllRoutesByDistance(float distance);
}

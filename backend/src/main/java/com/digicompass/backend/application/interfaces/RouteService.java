package com.digicompass.backend.application.interfaces;

import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.application.models.RouteGeometry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RouteService {
    List<Route> getRoutes();
    RouteGeometry getRouteById(Long id);
    List<Route> getRoutesByType(String type);
    List<Route> getRoutesByDistance(float distance);
    List<Route> getRoutesByKeyword(String keyword);
    List<Route> getRouteByDifficulty(String difficulty);
    List<Route> getFilteredRoutes(String type, String difficulty, Float distance);

}

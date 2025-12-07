package com.digicompass.backend.application.interfaces;

import com.digicompass.backend.application.models.GeoJson;
import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.application.models.RouteGeometry;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RouteService {
    List<Route> getRoutes();
    RouteGeometry getRouteById(Long id);
    List<Route> getFilteredRoutes(String type, String difficulty, Float distance);
    List<Route> searchRoutes(String keyword);
    List<Route> getLikedRoutesByUserId(Long userId);
    void saveRoute(Route route);
//    double calculateDistance(Geometry geometry);
    double calculateDistanceFromGeoJson(GeoJson geoJson);
}

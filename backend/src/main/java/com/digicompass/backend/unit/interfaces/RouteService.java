package com.digicompass.backend.unit.interfaces;

import com.digicompass.backend.unit.models.Route;
import com.digicompass.backend.unit.models.RouteGeometry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RouteService {
    List<Route> getRoutes();
    RouteGeometry getRouteById(Long id);
    List<Route> getFilteredRoutes(String type, String difficulty, Float distance);

}

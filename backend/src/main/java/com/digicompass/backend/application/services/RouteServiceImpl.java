package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.RouteService;
import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.domain.models.Route;
import com.digicompass.backend.domain.models.RouteGeometry;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.RouteInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteInterface routeRepository;
    private final RouteMapper routeMapper;
    private final ObjectMapper objectMapper;

    public RouteServiceImpl(RouteInterface routeRepository, RouteMapper routeMapper, ObjectMapper objectMapper) {
        this.routeRepository = routeRepository;
        this.routeMapper = routeMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Route> getRoutes() {
        return routeMapper.toDomain(routeRepository.getAllRoutes());
    }

    @Override
    public RouteGeometry getRouteById(Long id) {
        Route route = routeMapper.toDomain(routeRepository.findById(id));

        if (route == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Route not found with id: " + id);
        }

        GeoJsonWriter writer = new GeoJsonWriter();

        try {
            String geojson = writer.write(route.getRouteGeometry());
            return new RouteGeometry(
                    route.getId(),
                    route.getName(),
                    objectMapper.readTree(geojson)
            );
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to convert route geometry to GeoJSON",
                    e
            );
        }
    }

    @Override
    public List<Route> getRoutesByType(String type) {
        try{
            return routeMapper.toDomain(routeRepository.getAllRoutesByType(type));
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Route not found with type: " + type);
        }
    }

    @Override
    public List<Route> getRoutesByDistance(float distance) {
        try{
            return routeMapper.toDomain(routeRepository.getAllRoutesByDistance(distance));
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Route not found with distance: " + distance);
        }
    }

    @Override
    public List<Route> getRoutesByKeyword(String keyword) {
        try {
            return routeMapper.toDomain(routeRepository.getRoutesByKeyword(keyword));
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "There was a problem finding by keyword.",
                    e
            );
        }
    }

    @Override
    public List<Route> getRouteByDifficulty(String difficulty) {
        try{
            return routeMapper.toDomain(routeRepository.getAllRoutesByDifficulty(difficulty));
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Route not found with difficulty: " + difficulty);
        }
    }
}

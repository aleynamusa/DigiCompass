package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.RatingService;
import com.digicompass.backend.application.interfaces.RouteService;
import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.application.models.RouteGeometry;
import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
public class RouteServiceImpl implements RouteService {

    private final RouteJpaRepository routeRepository;
    private final RouteMapper routeMapper;
    private final ObjectMapper objectMapper;
    private final RatingService ratingService;


    public RouteServiceImpl(RouteJpaRepository routeRepository,
                            RouteMapper routeMapper,
                            ObjectMapper objectMapper,
                            RatingService ratingService) {
        this.routeRepository = routeRepository;
        this.routeMapper = routeMapper;
        this.objectMapper = objectMapper;
        this.ratingService = ratingService;
    }

    @Override
    public List<Route> getRoutes() {
        try {
            log.info("[SERVICE] Fetching all routes from the database.");

            List<Route> routes = routeMapper.toDomain(routeRepository.findAll());

            for (Route route : routes) {
                route.setAverageRating(ratingService.getRouteRating(route.getId()));
            }

            log.info("[SERVICE] Successfully fetched {} routes.", routes.size());
            return routes;

        } catch (Exception e) {
            log.error("[SERVICE] Error occurred while fetching routes: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch routes.", e);
        }
    }

    @Override
    public RouteGeometry getRouteById(Long id) {
        try {
            log.info("[SERVICE] Fetching route by id: {}", id);

            Route route = routeMapper.toDomain(routeRepository.findById(id).orElse(null));

            if (route == null || !routeRepository.getAllIds().contains(id)) {
                log.warn("[SERVICE] Route not found with id: {}", id);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Route not found with id: " + id);
            }

            GeoJsonWriter writer = new GeoJsonWriter();
            String geojson = writer.write(route.getRouteGeometry());

            return new RouteGeometry(
                    route.getId(),
                    route.getName(),
                    objectMapper.readTree(geojson)
//                    route.getReviews(),
//                    route.getRatings()
            );

        } catch (JsonProcessingException e) {
            log.error("[SERVICE] Failed to convert route geometry to GeoJSON for route id {}: {}",
                    id, e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to convert route geometry to GeoJSON.",
                    e
            );
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("[SERVICE] Unexpected error fetching route by id {}: {}",
                    id, e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error while fetching route by id.",
                    e
            );
        }
    }

    @Override
    public List<Route> getFilteredRoutes(String type, String difficulty, Float distance) {
        try {
            log.info("[SERVICE] Filtering routes with type={}, difficulty={}, distance={}",
                    type, difficulty, distance);

            List<Route> routes = routeMapper.toDomain(routeRepository.findFiltered(type, difficulty, distance));
            routes.forEach(r -> r.setRouteGeometry(null));

            log.info("[SERVICE] Filtered {} routes based on provided criteria.", routes.size());
            return routes;

        } catch (Exception e) {
            log.error("[SERVICE] Error filtering routes: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to filter routes.", e);
        }
    }

    @Override
    public List<Route> searchRoutes(String keyword) {
        try{
            log.info("[SERVICE] Searching routes with keyword: {}", keyword);

            List<Route> routes = routeMapper.toDomain(routeRepository.getRouteEntitiesByName(keyword));
            routes.forEach(r -> r.setRouteGeometry(null));

            log.info("[SERVICE] Successfully fetched {} routes.", routes.size());
            return routes;
        }
        catch (Exception e){
            log.error("[SERVICE] Error occurred while searching routes by keyword: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to filter routes.", e);
        }
    }
}

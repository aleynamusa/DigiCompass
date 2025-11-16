package com.digicompass.backend.unit.services;

import com.digicompass.backend.unit.interfaces.RatingService;
import com.digicompass.backend.unit.interfaces.RouteService;
import com.digicompass.backend.unit.mapper.RouteMapper;
import com.digicompass.backend.unit.models.Route;
import com.digicompass.backend.unit.models.RouteGeometry;
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
            log.info("Fetching all routes from the database.");

            List<Route> routes = routeMapper.toDomain(routeRepository.findAll());

            for (Route route : routes) {
                route.setAverageRating(ratingService.getRouteRating(route.getId()));
            }

            log.info("Successfully fetched {} routes.", routes.size());
            return routes;

        } catch (Exception e) {
            log.error("Error occurred while fetching routes: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch routes.", e);
        }
    }

    @Override
    public RouteGeometry getRouteById(Long id) {
        try {
            log.info("Fetching route by id: {}", id);

            Route route = routeMapper.toDomain(routeRepository.findById(id).orElse(null));

            if (route == null || !routeRepository.getAllIds().contains(id)) {
                log.warn("Route not found with id: {}", id);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Route not found with id: " + id);
            }

            GeoJsonWriter writer = new GeoJsonWriter();
            String geojson = writer.write(route.getRouteGeometry());

            return new RouteGeometry(
                    route.getId(),
                    route.getName(),
                    objectMapper.readTree(geojson),
                    route.getReviews(),
                    route.getRatings()
            );

        } catch (JsonProcessingException e) {
            log.error("Failed to convert route geometry to GeoJSON for route id {}: {}",
                    id, e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to convert route geometry to GeoJSON.",
                    e
            );
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error fetching route by id {}: {}",
                    new Object[]{id, e.getMessage()});
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
            log.info("Filtering routes with type={}, difficulty={}, distance={}",
                    new Object[]{type, difficulty, distance});

            List<Route> routes = routeMapper.toDomain(routeRepository.findFiltered(type, difficulty, distance));
            routes.forEach(r -> r.setRouteGeometry(null));

            log.info("Filtered {} routes based on provided criteria.", routes.size());
            return routes;

        } catch (Exception e) {
            log.error("Error filtering routes: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to filter routes.", e);
        }
    }
}

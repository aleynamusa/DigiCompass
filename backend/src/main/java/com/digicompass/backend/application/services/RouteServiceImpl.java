package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.RatingService;
import com.digicompass.backend.application.interfaces.ReviewService;
import com.digicompass.backend.application.interfaces.RouteService;
import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.application.models.Rating;
import com.digicompass.backend.application.models.Review;
import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.application.models.RouteGeometry;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.RouteInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class RouteServiceImpl implements RouteService {

    private static final Logger LOGGER = Logger.getLogger(RouteServiceImpl.class.getName());

    private final RouteInterface routeRepository;
    private final RouteMapper routeMapper;
    private final ObjectMapper objectMapper;
    private final RatingService ratingService;
    private final ReviewService reviewService;

    public RouteServiceImpl(RouteInterface routeRepository,
                            RouteMapper routeMapper,
                            ObjectMapper objectMapper,
                            RatingService ratingService,
                            ReviewService reviewService) {
        this.routeRepository = routeRepository;
        this.routeMapper = routeMapper;
        this.objectMapper = objectMapper;
        this.ratingService = ratingService;
        this.reviewService = reviewService;
    }

    @Override
    public List<Route> getRoutes() {
        try {
            LOGGER.log(Level.INFO, "Fetching all routes from the database.");

            List<Route> routes = routeMapper.toDomain(routeRepository.getAllRoutes());

            for (Route route : routes) {
                route.setAverageRating(ratingService.getRouteRating(route.getId()));
                route.setReviews(reviewService.getReviewsByRoute(route.getId()));
            }

            LOGGER.log(Level.INFO, "Successfully fetched {0} routes.", routes.size());
            return routes;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while fetching routes: {0}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch routes.", e);
        }
    }

    @Override
    public RouteGeometry getRouteById(Long id) {
        try {
            LOGGER.log(Level.INFO, "Fetching route by id: {0}", id);

            Route route = routeMapper.toDomain(routeRepository.findById(id));

            if (route == null || !routeRepository.getAllIds().contains(id)) {
                LOGGER.log(Level.WARNING, "Route not found with id: {0}", id);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Route not found with id: " + id);
            }

            List<Review> reviews = reviewService.getReviewsByRoute(route.getId());
            route.setReviews(reviews);

            List<Rating> ratings = ratingService.getRatingsByRouteId(id);
            route.setRatings(ratings);

            GeoJsonWriter writer = new GeoJsonWriter();
            String geojson = writer.write(route.getRouteGeometry());

            LOGGER.log(Level.INFO, "Fetched route {0} with {1} reviews and {2} ratings.",
                    new Object[]{id, reviews.size(), ratings.size()});

            return new RouteGeometry(
                    route.getId(),
                    route.getName(),
                    objectMapper.readTree(geojson),
                    route.getReviews(),
                    route.getRatings()
            );

        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Failed to convert route geometry to GeoJSON for route id {0}: {1}",
                    new Object[]{id, e.getMessage()});
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to convert route geometry to GeoJSON.",
                    e
            );
        } catch (ResponseStatusException e) {
            // Pass through for NOT_FOUND or other explicit HTTP errors
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error fetching route by id {0}: {1}",
                    new Object[]{id, e.getMessage()});
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error while fetching route by id.",
                    e
            );
        }
    }

    @Override
    public List<Route> getRoutesByType(String type) {
        try {
            LOGGER.log(Level.INFO, "Fetching routes by type: {0}", type);

            List<Route> routes = routeMapper.toDomain(routeRepository.getAllRoutesByType(type));
            routes.forEach(r -> r.setRouteGeometry(null));

            LOGGER.log(Level.INFO, "Found {0} routes for type: {1}", new Object[]{routes.size(), type});
            return routes;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching routes by type {0}: {1}", new Object[]{type, e.getMessage()});
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Route not found with type: " + type, e);
        }
    }

    @Override
    public List<Route> getRoutesByDistance(float distance) {
        try {
            LOGGER.log(Level.INFO, "Fetching routes by distance: {0}", distance);

            List<Route> routes = routeMapper.toDomain(routeRepository.getAllRoutesByDistance(distance));
            routes.forEach(r -> r.setRouteGeometry(null));

            LOGGER.log(Level.INFO, "Found {0} routes for distance: {1}", new Object[]{routes.size(), distance});
            return routes;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching routes by distance {0}: {1}", new Object[]{distance, e.getMessage()});
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Route not found with distance: " + distance, e);
        }
    }

    @Override
    public List<Route> getRoutesByKeyword(String keyword) {
        try {
            LOGGER.log(Level.INFO, "Fetching routes by keyword: {0}", keyword);

            List<Route> routes = routeMapper.toDomain(routeRepository.getRoutesByKeyword(keyword));
            routes.forEach(r -> r.setRouteGeometry(null));

            LOGGER.log(Level.INFO, "Found {0} routes matching keyword: {1}", new Object[]{routes.size(), keyword});
            return routes;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching routes by keyword {0}: {1}", new Object[]{keyword, e.getMessage()});
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "There was a problem finding routes by keyword.",
                    e
            );
        }
    }

    @Override
    public List<Route> getRouteByDifficulty(String difficulty) {
        try {
            LOGGER.log(Level.INFO, "Fetching routes by difficulty: {0}", difficulty);

            List<Route> routes = routeMapper.toDomain(routeRepository.getAllRoutesByDifficulty(difficulty));
            routes.forEach(r -> r.setRouteGeometry(null));

            LOGGER.log(Level.INFO, "Found {0} routes for difficulty: {1}", new Object[]{routes.size(), difficulty});
            return routes;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching routes by difficulty {0}: {1}", new Object[]{difficulty, e.getMessage()});
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Route not found with difficulty: " + difficulty, e);
        }
    }

    @Override
    public List<Route> getFilteredRoutes(String type, String difficulty, Float distance) {
        try {
            LOGGER.log(Level.INFO, "Filtering routes with type={0}, difficulty={1}, distance={2}",
                    new Object[]{type, difficulty, distance});

            List<Route> routes = routeMapper.toDomain(routeRepository.filterAll(type, difficulty, distance));
            routes.forEach(r -> r.setRouteGeometry(null));

            LOGGER.log(Level.INFO, "Filtered {0} routes based on provided criteria.", routes.size());
            return routes;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error filtering routes: {0}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to filter routes.", e);
        }
    }
}

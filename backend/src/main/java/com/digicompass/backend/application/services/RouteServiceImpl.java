package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.RatingService;
import com.digicompass.backend.application.interfaces.ReviewService;
import com.digicompass.backend.application.interfaces.RouteService;
import com.digicompass.backend.application.interfaces.S3Service;
import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.infrastucture.persistence.models.Rating;
import com.digicompass.backend.infrastucture.persistence.models.Review;
import com.digicompass.backend.infrastucture.persistence.models.Route;
import com.digicompass.backend.infrastucture.persistence.models.RouteGeometry;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.RouteInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteInterface routeRepository;
    private final RouteMapper routeMapper;
    private final ObjectMapper objectMapper;
    private final RatingService ratingService;
    private final ReviewService reviewService;
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteServiceImpl.class);


    public RouteServiceImpl(RouteInterface routeRepository, RouteMapper routeMapper, ObjectMapper objectMapper, RatingService ratingService, ReviewService reviewService) {
        this.routeRepository = routeRepository;
        this.routeMapper = routeMapper;
        this.objectMapper = objectMapper;
        this.ratingService = ratingService;
        this.reviewService = reviewService;
    }

    @Override
    public List<Route> getRoutes() {
        List<Route> routes = routeMapper.toDomain(routeRepository.getAllRoutes());
        for (Route route : routes) {
            route.setAverageRating(ratingService.getRouteRating(route.getId()));
            route.setReviews(reviewService.getReviewsByRoute(route.getId()));
        }

        return routes;
    }

    @Override
    public RouteGeometry getRouteById(Long id) {
        Route route = routeMapper.toDomain(routeRepository.findById(id));

        if (route == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Route not found with id: " + id);
        }

        List<Review> reviews = reviewService.getReviewsByRoute(route.getId());
        route.setReviews(reviews);

        List<Rating> ratings = ratingService.getRatingsByRouteId(id);
        route.setRatings(ratings);

        GeoJsonWriter writer = new GeoJsonWriter();

        try {
            String geojson = writer.write(route.getRouteGeometry());
            LOGGER.info("Reviews attached to route before returning: " + route.getReviews());

            return new RouteGeometry(
                    route.getId(),
                    route.getName(),
                    objectMapper.readTree(geojson),
                    route.getReviews(),
                    route.getRatings()
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
            List<Route> routes =routeMapper.toDomain(routeRepository.getAllRoutesByType(type));
            routes.forEach(r -> r.setRouteGeometry(null));
            return routes;
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Route not found with type: " + type);
        }
    }

    @Override
    public List<Route> getRoutesByDistance(float distance) {
        try{
            List<Route> routes =routeMapper.toDomain(routeRepository.getAllRoutesByDistance(distance));
            routes.forEach(r -> r.setRouteGeometry(null));
            return routes;
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Route not found with distance: " + distance);
        }
    }

    @Override
    public List<Route> getRoutesByKeyword(String keyword) {
        try {
            List<Route> routes =routeMapper.toDomain(routeRepository.getRoutesByKeyword(keyword));
            routes.forEach(r -> r.setRouteGeometry(null));
            return routes;
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
            List<Route> routes =routeMapper.toDomain(routeRepository.getAllRoutesByDifficulty(difficulty));
            routes.forEach(r -> r.setRouteGeometry(null));
            return routes;
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Route not found with difficulty: " + difficulty);
        }
    }

    @Override
    public List<Route> getFilteredRoutes(String type, String difficulty, Float distance) {
        try {
            List<Route> routes = routeMapper.toDomain(routeRepository.filterAll(type, difficulty, distance));
            routes.forEach(r -> r.setRouteGeometry(null));
            return routes;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to filter routes", e);
        }
    }



}

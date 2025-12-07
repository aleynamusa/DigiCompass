package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.RatingService;
import com.digicompass.backend.application.interfaces.RouteService;
import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.application.models.GeoJson;
import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.application.models.RouteGeometry;
import com.digicompass.backend.repository.repositories.FavouriteRouteJpaRepository;
import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
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
    private final UserJpaRepository userRepository;
    private final FavouriteRouteJpaRepository favouriteRouteRepository;


    public RouteServiceImpl(RouteJpaRepository routeRepository,
                            RouteMapper routeMapper,
                            ObjectMapper objectMapper,
                            RatingService ratingService, UserJpaRepository userRepository, FavouriteRouteJpaRepository favouriteRouteRepository) {
        this.routeRepository = routeRepository;
        this.routeMapper = routeMapper;
        this.objectMapper = objectMapper;
        this.ratingService = ratingService;
        this.userRepository = userRepository;
        this.favouriteRouteRepository = favouriteRouteRepository;
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
            for (Route route : routes) {
                route.setAverageRating(ratingService.getRouteRating(route.getId()));
            }
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
            for (Route route : routes) {
                route.setAverageRating(ratingService.getRouteRating(route.getId()));
            }
            routes.forEach(r -> r.setRouteGeometry(null));

            log.info("[SERVICE] Successfully fetched {} routes.", routes.size());
            return routes;
        }
        catch (Exception e){
            log.error("[SERVICE] Error occurred while searching routes by keyword: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to filter routes.", e);
        }
    }

    @Override
    public List<Route> getLikedRoutesByUserId(Long userId) {
        if(!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId);
        }
        try{
            List<Route> likedRoutes = routeMapper.toDomain(favouriteRouteRepository.findAllLikedRoutesByUserId(userId));
            for (Route route : likedRoutes) {
                route.setAverageRating(ratingService.getRouteRating(route.getId()));
            }
            likedRoutes.forEach(r -> r.setRouteGeometry(null));

            log.debug("[SERVICE] Successfully fetched {} liked routes for user id: {}", likedRoutes.size(), userId);

            return likedRoutes;
        }
        catch (Exception e){
            log.error("[SERVICE] Error occurred while fetching liked routes for user id {}: {}", userId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch liked routes.", e);
        }
    }

    @Override
    public void saveRoute(Route route) {
        try {
            log.info("[SERVICE] Saving new route: {}", route.getName());

            routeRepository.save(routeMapper.toEntity(route));

            log.info("[SERVICE] Successfully saved route: {}", route.getName());
        } catch (Exception e) {
            log.error("[SERVICE] Error occurred while saving route {}: {}", route.getName(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save route.", e);
        }
    }

    private double calculateDistance(Geometry geometry) {
        if (geometry == null) return 0;

        Coordinate[] coords = geometry.getCoordinates();
        if (coords.length < 2) return 0;

        double total = 0;

        for (int i = 1; i < coords.length; i++) {
            double lat1 = coords[i - 1].y;
            double lon1 = coords[i - 1].x;
            double lat2 = coords[i].y;
            double lon2 = coords[i].x;

            total += haversine(lat1, lon1, lat2, lon2);
        }

        return total; // km
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }


    private LineString buildLineStringFromGeoJson(GeoJson geoJson) {
        List<List<Double>> coordsList = geoJson.getCoordinates();

        if (coordsList == null || coordsList.size() < 2) {
            throw new IllegalArgumentException("LineString requires at least two points.");
        }

        GeometryFactory factory = new GeometryFactory();

        Coordinate[] coords = coordsList.stream()
                .map(c -> new Coordinate(c.get(0), c.get(1)))
                .toArray(Coordinate[]::new);

        return factory.createLineString(coords);
    }

    @Override
    public double calculateDistanceFromGeoJson(GeoJson geoJson) {
        try{
            if (geoJson == null || !"LineString".equals(geoJson.getType())) {
                throw new IllegalArgumentException("Invalid GeoJSON: Expected LineString type.");
            }
            LineString lineString = buildLineStringFromGeoJson(geoJson);
            return calculateDistance(lineString);
        }
        catch (Exception e){
            log.error("[SERVICE] Invalid GeoJSON provided: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid GeoJSON provided.", e);
        }

    }
}

package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.RatingService;
import com.digicompass.backend.application.interfaces.RouteService;
import com.digicompass.backend.application.interfaces.S3Service;
import com.digicompass.backend.application.mapper.CreateRouteMapper;
import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.application.models.*;
import com.digicompass.backend.application.models.route.Route;
import com.digicompass.backend.application.models.route.RouteGeometry;
import com.digicompass.backend.configuration.UserPrincipal;
import com.digicompass.backend.repository.entity.RouteEntity;
import com.digicompass.backend.repository.entity.RouteImageEntity;
import com.digicompass.backend.repository.repositories.FavouriteRouteJpaRepository;
import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
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
    private final UserMapper userMapper;
    private final S3Service s3Service;
    private final CreateRouteMapper createRouteMapper;



    public RouteServiceImpl(RouteJpaRepository routeRepository,
                            RouteMapper routeMapper,
                            ObjectMapper objectMapper,
                            RatingService ratingService, UserJpaRepository userRepository, FavouriteRouteJpaRepository favouriteRouteRepository, UserMapper userMapper, S3Service s3Service, CreateRouteMapper createRouteMapper) {
        this.routeRepository = routeRepository;
        this.routeMapper = routeMapper;
        this.objectMapper = objectMapper;
        this.ratingService = ratingService;
        this.userRepository = userRepository;
        this.favouriteRouteRepository = favouriteRouteRepository;
        this.userMapper = userMapper;
        this.s3Service = s3Service;
        this.createRouteMapper = createRouteMapper;
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
    @Transactional
    public void saveRoute(Route route, List<MultipartFile> images, Long userId) throws IOException {

        log.info("[SERVICE] Saving new route: {}", route.getName());

        List<String> uploadedKeys = new ArrayList<>();

        try {
            User user = userMapper.toDomain(
                    userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("User not found"))
            );

            route.setCreatedByUserId(user);

            RouteEntity routeEntity = createRouteMapper.toEntity(route);
            RouteEntity savedRoute = routeRepository.save(routeEntity);

            if (images != null && !images.isEmpty()) {
                uploadedKeys = s3Service.uploadImages(savedRoute.getId(), images);

                for (String key : uploadedKeys) {
                    RouteImageEntity imageEntity = new RouteImageEntity();
                    imageEntity.setImageUrl(key);
                    imageEntity.setRoute(savedRoute);
                    savedRoute.getImages().add(imageEntity);
                }
            }

            routeRepository.save(savedRoute);
            log.info("[SERVICE] Successfully saved route id={}", savedRoute.getId());

        } catch (Exception e) {
            log.error("[SERVICE] Error saving route, rolling back S3 uploads", e);
            s3Service.rollbackS3Uploads(uploadedKeys);
            throw e;
        }
    }


    @Override
    public void deleteRoute(Long id, UserPrincipal principal) {

        RouteEntity route = routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Route not found"));

        Long creatorId = route.getCreatedByUserId().getId();
        Long currentUserId = principal.getId();

        boolean isAdmin = principal.hasRole("ROLE_ADMIN") || principal.hasRole("ADMIN");
        boolean isCreator = creatorId.equals(currentUserId);

        if (!isAdmin && !isCreator) {
            log.warn("[SERVICE] User {} is not allowed to delete route {}", currentUserId, id);
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not allowed to delete this route"
            );
        }

        List<RouteImageEntity> images = route.getImages();
        for (RouteImageEntity image : images) {
            s3Service.rollbackS3Upload(image.getImageUrl());
        }

        routeRepository.delete(route);

        log.info("[SERVICE] Route {} deleted by user {}", id, currentUserId);
    }


    @Override
    public void updateRoute(Route route, List<MultipartFile> images, Long id) throws IOException {

    }

}

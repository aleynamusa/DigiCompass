package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.UserActionsService;
import com.digicompass.backend.application.mapper.FavouriteRouteMapper;
import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.application.models.FavouriteRoute;
import com.digicompass.backend.repository.entity.FavouriteRouteKey;
import com.digicompass.backend.repository.repositories.FavouriteRouteJpaRepository;
import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class UserActionsServiceImpl implements UserActionsService {

    private final FavouriteRouteJpaRepository favouriteRouteRepository;
    private final UserJpaRepository userRepository;
    private final RouteJpaRepository routeRepository;
    private final UserMapper userMapper;
    private final FavouriteRouteMapper favouriteRouteMapper;
    private final RouteMapper routeMapper;

    public UserActionsServiceImpl(FavouriteRouteJpaRepository favouriteRouteRepository, UserJpaRepository userRepository, RouteJpaRepository routeRepository, UserMapper userMapper, FavouriteRouteMapper favouriteRouteMapper, RouteMapper routeMapper) {
        this.favouriteRouteRepository = favouriteRouteRepository;
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
        this.userMapper = userMapper;
        this.favouriteRouteMapper = favouriteRouteMapper;
        this.routeMapper = routeMapper;
    }


    @Override
    public void favouriteRoute(Long userId, Long routeId) {
        log.info("[SERVICE] FavouriteRoute called with userId={} routeId={}", userId, routeId);
        if (!userRepository.existsById(userId) || !routeRepository.existsById(routeId)) {
            log.warn("[SERVICE] User or route does not exist. userId={} routeId={}", userId, routeId);
            throw new IllegalArgumentException("User or route does not exist.");
        }
        try {
            FavouriteRoute favouriteRoute = new FavouriteRoute(
                    userMapper.toDomain(userRepository.getById(userId)),
                    routeMapper.toDomain(routeRepository.getById(routeId)),
                    LocalDateTime.now()
            );
            favouriteRouteRepository.save(favouriteRouteMapper.toEntity(favouriteRoute));
            log.debug("[SERVICE] Saved favourite route for userId={} routeId={}", userId, routeId);
        } catch (Exception e) {
            log.error("[SERVICE] Error liking the route for userId={} routeId={}", userId, routeId, e);
            throw new RuntimeException("Error liking the route.", e);
        }
    }


    @Override
    public void unfavouriteRoute(Long userId, Long routeId) {
        log.info("[SERVICE] UnfavouriteRoute called with userId={} routeId={}", userId, routeId);
        if (!userRepository.existsById(userId) || !routeRepository.existsById(routeId)) {
            log.warn("[SERVICE] User or route does not exist. userId={} routeId={}", userId, routeId);
            throw new IllegalArgumentException("User or route does not exist.");
        }
        try {
            FavouriteRoute favouriteRoute = new FavouriteRoute(
                    userMapper.toDomain(userRepository.getById(userId)),
                    routeMapper.toDomain(routeRepository.getById(routeId)),
                    LocalDateTime.now()
            );
            favouriteRouteRepository.delete(favouriteRouteMapper.toEntity(favouriteRoute));
            log.debug("[SERVICE] Deleted favourite route for userId={} routeId={}", userId, routeId);
        } catch (Exception e) {
            log.error("[SERVICE] Error unliking the route for userId={} routeId={}", userId, routeId, e);
            throw new RuntimeException("Error unliking the route.", e);
        }
    }

    @Override
    public boolean isLikedRoute(Long userId, Long routeId) {
        if (!userRepository.existsById(userId) || !routeRepository.existsById(routeId)) {
            log.warn("[SERVICE] User or route does not exist. userId={} routeId={}", userId, routeId);
            throw new IllegalArgumentException("User or route does not exist.");
        }
        try {

            boolean isLiked = favouriteRouteRepository.existsByIdUserIdAndIdRouteId(userId, routeId);
            log.debug("[SERVICE] isLikedRoute for userId={} routeId={} : {}", userId, routeId, isLiked);
            return isLiked;
        } catch (Exception e) {
            log.error("[SERVICE] Error checking if the routeId {} was liked by userId {}", routeId, userId, e);
            throw new RuntimeException("Error unliking the route.", e);
        }
    }
}

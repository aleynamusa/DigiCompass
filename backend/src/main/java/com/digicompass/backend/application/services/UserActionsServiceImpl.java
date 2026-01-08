package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.UserActionsService;
import com.digicompass.backend.application.mapper.FavouriteRouteMapper;
import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.application.models.FavouriteRoute;
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
        try {
            FavouriteRoute favouriteRoute = buildFavouriteRoute(userId, routeId);
            favouriteRouteRepository.save(favouriteRouteMapper.toEntity(favouriteRoute));
            log.debug("[SERVICE] Saved favourite route for userId={} routeId={}", userId, routeId);
        } catch (IllegalArgumentException e) {
            log.warn("[SERVICE] User or route does not exist. userId={} routeId={}, reason={}", userId, routeId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("[SERVICE] Error liking the route for userId={} routeId={}", userId, routeId, e);
            throw new RuntimeException("Error liking the route.", e);
        }
    }

    @Override
    public void unfavouriteRoute(Long userId, Long routeId) {
        log.info("[SERVICE] UnfavouriteRoute called with userId={} routeId={}", userId, routeId);
        try {
            FavouriteRoute favouriteRoute = buildFavouriteRoute(userId, routeId);
            favouriteRouteRepository.delete(favouriteRouteMapper.toEntity(favouriteRoute));
            log.debug("[SERVICE] Deleted favourite route for userId={} routeId={}", userId, routeId);
        } catch (IllegalArgumentException e) {
            log.warn("[SERVICE] User or route does not exist. userId={} routeId={}, reason={}", userId, routeId, e.getMessage());
            throw new IllegalArgumentException("User or route does not exist.", e);
        } catch (Exception e) {
            log.error("[SERVICE] Error unliking the route for userId={} routeId={}", userId, routeId, e);
            throw new RuntimeException("Error unliking the route.", e);
        }
    }

    @Override
    public boolean isLikedRoute(Long userId, Long routeId) {
        try {
            ensureUserAndRouteExist(userId, routeId);
            boolean isLiked = favouriteRouteRepository.existsByIdUserIdAndIdRouteId(userId, routeId);
            log.debug("[SERVICE] isLikedRoute for userId={} routeId={} : {}", userId, routeId, isLiked);
            return isLiked;
        } catch (IllegalArgumentException e) {
            log.warn("[SERVICE] User or route does not exist. userId={} routeId={}, reason={}", userId, routeId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("[SERVICE] Error checking if the routeId {} was liked by userId {}", routeId, userId, e);
            throw new RuntimeException("Error checking liked state.", e);
        }
    }

    protected FavouriteRoute buildFavouriteRoute(Long userId, Long routeId) {
        var userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist."));
        var routeEntity = routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Route does not exist."));
        return new FavouriteRoute(
                userMapper.toDomain(userEntity),
                routeMapper.toDomain(routeEntity),
                LocalDateTime.now()
        );
    }

    private void ensureUserAndRouteExist(Long userId, Long routeId) {
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User does not exist."));
        routeRepository.findById(routeId).orElseThrow(() -> new IllegalArgumentException("Route does not exist."));
    }
}

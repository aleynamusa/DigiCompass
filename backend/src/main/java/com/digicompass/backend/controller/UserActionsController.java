package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.UserActionsService;
import com.digicompass.backend.controller.dto.request.FavouriteRouteRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/action")
@Slf4j
public class UserActionsController {
    private final UserActionsService userActionsService;

    @Autowired
    public UserActionsController(UserActionsService userActionsService) {
        this.userActionsService = userActionsService;
    }

    @PostMapping("/favorite")
    public ResponseEntity<String> favoriteRoute(@RequestBody FavouriteRouteRequest request) {
        log.info("[CONTROLLER] Favorite route called. userId={}, routeId={}", request.getUserId(), request.getRouteId());
            userActionsService.favouriteRoute(request.getUserId(), request.getRouteId());
            log.info("[CONTROLLER] Favorite route succeeded. userId={}, routeId={}", request.getUserId(), request.getRouteId());
            return ResponseEntity.ok("Route favorited successfully.");

    }

    @PostMapping("/unfavorite")
    public ResponseEntity<String> unfavoriteRoute(@RequestBody FavouriteRouteRequest request) {
        log.info("[CONTROLLER] Unfavorite route called. userId={}, routeId={}", request.getUserId(), request.getRouteId());

            userActionsService.unfavouriteRoute(request.getUserId(), request.getRouteId());
            log.info("[CONTROLLER] Unfavorite route succeeded. userId={}, routeId={}", request.getUserId(), request.getRouteId());
            return ResponseEntity.ok("Route unfavorited successfully.");

    }

    @GetMapping("/isLiked")
    public ResponseEntity<Boolean> isLikedRoute(@RequestParam Long userId,
                                                @RequestParam Long routeId) {
        log.info("[CONTROLLER] IsLikedRoute called. userId={}, routeId={}", userId, routeId);
            boolean isLiked = userActionsService.isLikedRoute(userId, routeId);
            log.info("[CONTROLLER] IsLikedRoute succeeded. userId={}, routeId={}, isLiked={}", userId, routeId, isLiked);

            return ResponseEntity.ok(isLiked);

    }
}

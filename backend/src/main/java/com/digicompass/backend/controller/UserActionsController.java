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
    public ResponseEntity<?> favoriteRoute(@RequestBody FavouriteRouteRequest request) {
        log.info("[CONTROLLER] Favorite route called. userId={}, routeId={}", request.getUserId(), request.getRouteId());
        try {
            userActionsService.favouriteRoute(request.getUserId(), request.getRouteId());
            log.info("[CONTROLLER] Favorite route succeeded. userId={}, routeId={}", request.getUserId(), request.getRouteId());
            return ResponseEntity.ok("Route favorited successfully.");
        } catch (IllegalArgumentException e) {
            log.warn("[CONTROLLER] Favorite route failed due to invalid input. userId={}, routeId={}, reason={}", request.getUserId(), request.getRouteId(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("[CONTROLLER] Favorite route failed. userId={}, routeId={}", request.getUserId(), request.getRouteId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/unfavorite")
    public ResponseEntity<?> unfavoriteRoute(@RequestBody FavouriteRouteRequest request) {
        log.info("[CONTROLLER] Unfavorite route called. userId={}, routeId={}", request.getUserId(), request.getRouteId());
        try {
            userActionsService.unfavouriteRoute(request.getUserId(), request.getRouteId());
            log.info("[CONTROLLER] Unfavorite route succeeded. userId={}, routeId={}", request.getUserId(), request.getRouteId());
            return ResponseEntity.ok("Route unfavorited successfully.");
        } catch (IllegalArgumentException e) {
            log.warn("[CONTROLLER] Unfavorite route failed due to invalid input. userId={}, routeId={}, reason={}", request.getUserId(), request.getRouteId(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("[CONTROLLER] Unfavorite route failed. userId={}, routeId={}", request.getUserId(), request.getRouteId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @GetMapping("/isLiked")
    public ResponseEntity<?> isLikedRoute(@RequestParam Long userId,
                                          @RequestParam Long routeId) {
        log.info("[CONTROLLER] IsLikedRoute called. userId={}, routeId={}", userId, routeId);
        try{
            boolean isLiked = userActionsService.isLikedRoute(userId, routeId);
            log.info("[CONTROLLER] IsLikedRoute succeeded. userId={}, routeId={}, isLiked={}", userId, routeId, isLiked);
            return ResponseEntity.ok(isLiked);
        } catch (IllegalArgumentException e) {
            log.warn("[CONTROLLER] IsLikedRoute failed due to invalid input. userId={}, routeId={}, reason={}", userId, routeId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("[CONTROLLER] IsLikedRoute failed. userId={}, routeId={}", userId, routeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
//
    }
}

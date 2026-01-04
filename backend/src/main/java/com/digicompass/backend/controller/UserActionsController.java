package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.UserActionsService;
import com.digicompass.backend.configuration.UserPrincipal;
import com.digicompass.backend.controller.dto.request.FavouriteRouteRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<String> favoriteRoute(@RequestParam Long routeId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[CONTROLLER] Favorite route called. userId={}, routeId={}",  userPrincipal.getId(), routeId);
            userActionsService.favouriteRoute( userPrincipal.getId(), routeId);
            log.info("[CONTROLLER] Favorite route succeeded. userId={}, routeId={}",  userPrincipal.getId(), routeId);
            return ResponseEntity.ok("Route favorited successfully.");

    }

    @PostMapping("/unfavorite")
    public ResponseEntity<String> unfavoriteRoute(@RequestParam Long routeId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("[CONTROLLER] Unfavorite route called. userId={}, routeId={}",  userPrincipal.getId(), routeId);

            userActionsService.unfavouriteRoute( userPrincipal.getId(), routeId);
            log.info("[CONTROLLER] Unfavorite route succeeded. userId={}, routeId={}", userPrincipal.getId(), userPrincipal.getId());
            return ResponseEntity.ok("Route unfavorited successfully.");

    }

    @GetMapping("/isLiked")
    public ResponseEntity<Boolean> isLikedRoute(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                @RequestParam Long routeId) {
        log.info("[CONTROLLER] IsLikedRoute called. userId={}, routeId={}", userPrincipal.getId(), routeId);
            boolean isLiked = userActionsService.isLikedRoute(userPrincipal.getId(), routeId);
            log.info("[CONTROLLER] IsLikedRoute succeeded. userId={}, routeId={}, isLiked={}",  userPrincipal.getId(), routeId, isLiked);

            return ResponseEntity.ok(isLiked);

    }
}

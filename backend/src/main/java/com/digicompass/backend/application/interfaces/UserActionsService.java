package com.digicompass.backend.application.interfaces;

public interface UserActionsService {
    void favouriteRoute(Long userId, Long routeId);
    void unfavouriteRoute(Long userId, Long routeId);
    boolean isLikedRoute(Long userId, Long routeId);
}

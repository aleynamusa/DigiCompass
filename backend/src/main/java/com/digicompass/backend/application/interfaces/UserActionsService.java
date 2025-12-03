package com.digicompass.backend.application.interfaces;

public interface UserActionsService {
    void FavouriteRoute(Long userId, Long routeId);
    void UnfavouriteRoute(Long userId, Long routeId);
}

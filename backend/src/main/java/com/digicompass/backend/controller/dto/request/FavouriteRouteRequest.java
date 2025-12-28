package com.digicompass.backend.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavouriteRouteRequest {
    @NotNull(message = "UserId cannot be null")
    private Long userId;

    @NotNull(message = "RouteId cannot be null")
    private Long routeId;
}
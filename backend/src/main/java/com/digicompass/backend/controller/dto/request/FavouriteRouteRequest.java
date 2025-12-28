package com.digicompass.backend.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class FavouriteRouteRequest {
    @NotNull(message = "UserId cannot be null")
    @Positive(message = "UserId must be a positive number")
    private Long userId;

    @NotNull(message = "RouteId cannot be null")
    @Positive(message = "RouteId must be a positive number")
    private Long routeId;
}
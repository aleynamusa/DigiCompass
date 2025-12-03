package com.digicompass.backend.controller.dto.request;

import lombok.Data;

@Data
public class FavouriteRouteRequest {
    private Long userId;
    private Long routeId;
}
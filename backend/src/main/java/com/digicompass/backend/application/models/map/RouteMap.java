package com.digicompass.backend.application.models.map;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class RouteMap {
    private List<Point> route;
    private double distanceKm;
    private long durationMin;
}

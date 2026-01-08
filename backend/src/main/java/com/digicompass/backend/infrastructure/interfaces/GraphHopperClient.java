package com.digicompass.backend.infrastructure.interfaces;

import com.digicompass.backend.application.models.map.Point;
import com.digicompass.backend.infrastructure.objects.graph.GraphHopperPathObject;

import java.util.List;

public interface GraphHopperClient {
    GraphHopperPathObject fetchSegment(
            List<Point> points,
            String routeType);
    String getCurrentLocationAsCity(double latitude, double longitude);
}

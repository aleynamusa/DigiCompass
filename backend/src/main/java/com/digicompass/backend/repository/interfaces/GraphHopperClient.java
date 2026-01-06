package com.digicompass.backend.repository.interfaces;

import com.digicompass.backend.application.models.map.Point;
import com.digicompass.backend.repository.entity.graph.GraphHopperPathEntity;

import java.util.List;

public interface GraphHopperClient {
    GraphHopperPathEntity fetchSegment(
            List<Point> points,
            String routeType);
    String getCurrentLocationAsCity(double latitude, double longitude);
}

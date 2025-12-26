package com.digicompass.backend.application.interfaces;

import com.digicompass.backend.application.models.map.Point;
import com.digicompass.backend.application.models.map.RouteMap;

import java.util.List;


public interface GraphHopperService {

    RouteMap calculateRoute(
            List<Point> points,
            String routeType
    );
}

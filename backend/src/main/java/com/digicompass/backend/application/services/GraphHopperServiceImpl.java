package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.GraphHopperService;
import com.digicompass.backend.application.mapper.GraphHopperMapper;
import com.digicompass.backend.application.models.map.GraphHopperPath;
import com.digicompass.backend.application.models.map.Point;
import com.digicompass.backend.application.models.map.RouteMap;
import com.digicompass.backend.repository.interfaces.GraphHopperClient;
import com.google.maps.internal.PolylineEncoding;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class GraphHopperServiceImpl implements GraphHopperService {
    private final GraphHopperClient  graphHopperClient;
    private final GraphHopperMapper graphHopperMapper;

    @Autowired
    public GraphHopperServiceImpl(
            GraphHopperClient graphHopperClient, GraphHopperMapper graphHopperMapper
    ) {
        this.graphHopperClient = graphHopperClient;
        this.graphHopperMapper = graphHopperMapper;
    }

    @Override
    public RouteMap calculateRoute(
            List<Point> points,
            String routeType
    ) {
        try{
            if (points.size() > 5) {
                log.error("[SERVICE] Not enough points for calculating route");
                throw new IllegalArgumentException("You have to select maximum 5 points");
            }

            log.info("[SERVICE] Calculating Route distance and time.");

            List<List<Point>> segments = segmentPoints(points, 5);

            List<Point> allCoords = new ArrayList<>();
            double totalDistance = 0;
            long totalTime = 0;

            for (int i = 0; i < segments.size(); i++) {
                GraphHopperPath path = graphHopperMapper.getGraphHopperPath(graphHopperClient.fetchSegment(
                        segments.get(i),
                        routeType
                ));

                List<Point> decoded = decodePolyline(path.getPoints());

                if (i > 0 && !decoded.isEmpty()) {
                    decoded.remove(0);
                }

                allCoords.addAll(decoded);
                totalDistance += path.getDistance();
                log.debug("[SERVICE] Segment {} distance: {} meters", i + 1, path.getDistance());
                totalTime += path.getTime();
                log.debug("[SERVICE] Segment {} time: {} meters", i + 1, path.getTime());
            }

            log.info("[SERVICE] Total distance {} meters", totalDistance);
            log.info("[SERVICE] Total time {} ms", totalTime);

            return new RouteMap(
                    allCoords,
                    totalDistance / 1000.0,
                    getDurationHour(totalTime)
            );

        }
        catch(IllegalArgumentException e){
            log.error("Error calculating route", e);
            throw new IllegalArgumentException("Error calculating route");
        }
        catch(Exception e){
            log.error("[SERVICE] Calculating Route distance and time.", e);
            throw new RuntimeException("Error calculating route: " + e.getMessage());
        }

    }

    @Override
    public String getCurrentLocationAsCity(double latitude, double longitude) {
        return graphHopperClient.getCurrentLocationAsCity(latitude, longitude);
    }


    private String getDurationHour(long durationMin) {
        long totalHours = durationMin / 3_600_000;
        long remainingMs = durationMin % 3_600_000;
        long totalMinutes = remainingMs / 60000;

        if (totalHours == 0){
            log.info("[SERVICE] Total Mins: {}", totalMinutes);
            return totalMinutes + "m";
        }
        else{
            log.info("[SERVICE] Total Hours: {} and Total Mins: {}", totalHours, totalMinutes);
            return totalHours + "h " + totalMinutes + "m";
        }
    }



    private List<List<Point>> segmentPoints(List<Point> points, int max) {
        try{
            log.info("[SERVICE] Segmenting points into chunks of maximum size: {}", max);
            if (points.size() <= max) return List.of(points);

            List<List<Point>> segments = new ArrayList<>();
            for (int i = 0; i < points.size() - 1; i += max - 1) {
                segments.add(points.subList(i, Math.min(i + max, points.size())));
            }

            log.debug("[SERVICE] Total segments created: {}", segments.size());
            return segments;
        }
        catch(Exception e){
            log.error("[SERVICE] Error segmenting points", e);
            throw new RuntimeException("Error segmenting points: " + e.getMessage());
        }

    }

    private List<Point> decodePolyline(String encoded) {
        try{
            log.info("[SERVICE] Decoding polyline");
            if (encoded == null || encoded.isEmpty()) {
                log.warn("[SERVICE] Encoded polyline is null or empty");
                return new ArrayList<>();
            }

            return PolylineEncoding.decode(encoded)
                    .stream()
                    .map(p -> new Point(p.lat, p.lng))
                    .toList();
        }
        catch(Exception e){
            log.error("[SERVICE] Error decoding polyline", e);
            throw new RuntimeException("Error decoding polyline: " + e.getMessage());
        }
    }

}



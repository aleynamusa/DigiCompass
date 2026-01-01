package com.digicompass.backend.application.services;


import com.digicompass.backend.application.interfaces.GraphHopperService;

import com.digicompass.backend.application.models.map.GraphHopper;
import com.digicompass.backend.application.models.map.GraphHopperPath;
import com.digicompass.backend.application.models.map.Point;
import com.digicompass.backend.application.models.map.RouteMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.internal.PolylineEncoding;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GraphHopperServiceImpl implements GraphHopperService {
    private final WebClient webClient;
    private final WebClient webClientReverse;
    private final ObjectMapper objectMapper;
    @Value("${graphhopper.api-key}")
    private String apiKey;

    private static final Map<String, String> PROFILE_MAP = Map.of(
            "walk", "foot",
            "bike", "bike",
            "car", "car"
    );

    @Autowired
    public GraphHopperServiceImpl(
            WebClient.Builder builder,
            @Value("${graphhopper.base-url}") String baseUrl,
            @Value("${graphhopper.reverse.base-url}") String reverseUrl, ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
        this.webClientReverse = builder.baseUrl(reverseUrl).build();
        this.webClient = builder.baseUrl(baseUrl).build();
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
                GraphHopperPath path = fetchSegment(
                        segments.get(i),
                        routeType,
                        apiKey
                );

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
    public String getCurrentLocationAsCity(double latitude, double longitude){
        try {
            log.info("[SERVICE] Fetching Current City");

            UriComponentsBuilder uri = UriComponentsBuilder.newInstance()
                    .queryParam("point", latitude + "," + longitude)
                    .queryParam("reverse", true)
                    .queryParam("key", apiKey);

            String response = webClientReverse.get()
                    .uri(uri.build().toUriString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            JsonNode hits = root.path("hits");

            if (hits.isArray() && hits.size() > 0) {
                for (JsonNode hit : hits) {
                    JsonNode cityNode = hit.get("city");
                    if (cityNode != null && !cityNode.isNull()) {
                        return cityNode.asText();
                    }
                }
            }

            return "Unknown city";

        } catch (Exception e) {
            log.error("[SERVICE] Error fetching current city information", e);
            throw new RuntimeException("Error fetching current city information: " + e.getMessage());
        }
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

    private GraphHopperPath fetchSegment(
            List<Point> points,
            String routeType,
            String apiKey
    ) {
        String profile = PROFILE_MAP.getOrDefault(routeType, "foot");

        try{
            log.info("Fetching segment from GraphHopper with profile: {}", profile);

            UriComponentsBuilder uri = UriComponentsBuilder.newInstance()
                    .queryParam("profile", profile)
                    .queryParam("points_encoded", true)
                    .queryParam("locale", "en")
                    .queryParam("key", apiKey);

            points.forEach(p ->
                    uri.queryParam("point", p.getLat() + "," + p.getLng())
            );


            return webClient.get()
                    .uri(uri.build().toUriString())
                    .retrieve()
                    .bodyToMono(GraphHopper.class)
                    .block()
                    .getPaths()
                    .get(0);

        }
        catch(Exception e){
            log.error("[SERVICE] Error logging profile information", e);
            throw new RuntimeException("Error logging profile information: " + e.getMessage());
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

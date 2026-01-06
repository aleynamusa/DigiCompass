package com.digicompass.backend.repository.externalAPIs;

import com.digicompass.backend.application.models.map.Point;
import com.digicompass.backend.repository.entity.graph.GraphHopperEntity;
import com.digicompass.backend.repository.entity.graph.GraphHopperPathEntity;
import com.digicompass.backend.repository.interfaces.GraphHopperClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class GraphHopperGateway implements GraphHopperClient {
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
    public GraphHopperGateway(
            WebClient.Builder builder,
            @Value("${graphhopper.base-url}") String baseUrl,
            @Value("${graphhopper.reverse.base-url}") String reverseUrl, ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
        this.webClientReverse = builder.baseUrl(reverseUrl).build();
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public GraphHopperPathEntity fetchSegment(
            List<Point> points,
            String routeType) {
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
                    .bodyToMono(GraphHopperEntity.class)
                    .block()
                    .getPaths()
                    .get(0);

        }
        catch(Exception e){
            log.error("[SERVICE] Error logging profile information", e);
            throw new RuntimeException("Error logging profile information: " + e.getMessage());
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
}


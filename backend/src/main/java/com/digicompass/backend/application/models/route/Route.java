package com.digicompass.backend.application.models.route;

import com.digicompass.backend.application.models.User;
import com.digicompass.backend.types.Difficulty;
import com.digicompass.backend.types.RouteType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Geometry;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Route {
    private Long id;
    private String name;
    private String description;
    private RouteType routeType;   //HIKING, CYCLING, RUNNING, WALKING
    private Difficulty difficulty;  // EASY, MEDIUM, HARD
    private float distance;
    private String duration;
    private User createdByUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonIgnore
    private Geometry routeGeometry;

    private List<String> images = new ArrayList<>();

    private String averageRating;

    private Double startLatitude;
    private Double startLongitude;
}



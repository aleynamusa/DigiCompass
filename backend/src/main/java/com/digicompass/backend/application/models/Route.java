package com.digicompass.backend.application.models;

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
    private String routeType;   //HIKING, CYCLING, RUNNING, WALKING
    private String difficulty;  //BEGINNER, EASY, MODERATE, HARD, EXPERT, EXTREME
    private float distance;
    private String duration;
    private User createdByUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonIgnore
    private Geometry routeGeometry;

    private List<String> images = new ArrayList<>();
    private List<String> reviews = new ArrayList<>();
    private List<Double> ratings = new ArrayList<>();

    private String averageRating;
}



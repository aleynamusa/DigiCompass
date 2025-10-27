package com.digicompass.backend.infrastucture.persistence.models;

import com.digicompass.backend.domain.entity.RatingEntity;
import com.digicompass.backend.domain.entity.ReviewEntity;
import com.digicompass.backend.domain.entity.RouteImageEntity;
import com.digicompass.backend.domain.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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
    private List<Review> reviews = new ArrayList<>();
    private List<Rating> ratings = new ArrayList<>();

    private Double averageRating;
}



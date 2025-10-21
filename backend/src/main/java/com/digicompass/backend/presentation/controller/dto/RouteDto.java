package com.digicompass.backend.presentation.controller.dto;

import com.digicompass.backend.infrastucture.persistence.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Geometry;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RouteDto {
    private Long id;
    private String name;
    private String description;
    private String routeType;   //HIKING, CYCLING, RUNNING, WALKING
    private String difficulty;  //BEGINNER, EASY, MODERATE, HARD, EXPERT, EXTREME
    private float distance;
    private String duration;
    private UserEntity createdByUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Geometry routeGeometry;
}

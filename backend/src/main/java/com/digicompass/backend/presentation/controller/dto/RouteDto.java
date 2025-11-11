package com.digicompass.backend.presentation.controller.dto;


import com.digicompass.backend.domain.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Geometry;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private UserDto createdByUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Geometry routeGeometry;
    private List<String> images = new ArrayList<>();
}

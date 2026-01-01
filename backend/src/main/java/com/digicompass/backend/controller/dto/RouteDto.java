package com.digicompass.backend.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RouteDto {
    private Long id;
    private String name;
    private String description;
    private String routeType;
    private String difficulty;
    private float distance;
    private String duration;
    private String averageRating;
    private UserDto createdByUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> images;

    private Double startLatitude;
    private Double startLongitude;

}

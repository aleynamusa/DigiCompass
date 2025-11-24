package com.digicompass.backend.controller.dto;


import com.digicompass.backend.application.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String routeType;
    private String difficulty;
    private float distance;
    private String duration;
    private String averageRating;
    private UserDto createdByUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> images;

}

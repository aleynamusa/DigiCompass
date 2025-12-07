package com.digicompass.backend.controller.dto.request;

import com.digicompass.backend.controller.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequestDto {
    private Long id;
    private String name;
    private String description;
    private String routeType;
    private String difficulty;
    private float distance;        // <-- ADD THIS
    private String duration;       // <-- ADD THIS
    private String geometry;       // <-- ADD THIS (frontend sends GeoJSON)
    private UserDto createdByUserId;
    private List<String> images;
}

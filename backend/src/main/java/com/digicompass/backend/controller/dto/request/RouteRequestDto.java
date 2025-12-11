package com.digicompass.backend.controller.dto.request;

import com.digicompass.backend.controller.dto.GeoJsonDto;
import com.digicompass.backend.controller.dto.UserDto;
import com.digicompass.backend.types.Difficulty;
import com.digicompass.backend.types.RouteType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequestDto {
    private Long id;
    private String name;
    private String description;
    private RouteType routeType;
    private Difficulty difficulty;
    private float distance;
    private String duration;
    private GeoJsonDto geometry;
    private UserDto createdByUserId;
    private List<String> images;
}

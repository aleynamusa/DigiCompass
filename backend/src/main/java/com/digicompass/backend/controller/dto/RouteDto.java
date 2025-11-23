package com.digicompass.backend.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}

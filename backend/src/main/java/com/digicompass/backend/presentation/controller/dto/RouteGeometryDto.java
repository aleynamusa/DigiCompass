package com.digicompass.backend.presentation.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteGeometryDto {
    private Long id;
    private String name;
    private Object geojson;
    private List<String> reviews;
    private List<Double> ratings;
}

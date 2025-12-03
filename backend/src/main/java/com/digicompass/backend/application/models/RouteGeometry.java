package com.digicompass.backend.application.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteGeometry {
    private Long id;
    private String name;
    private Object geojson;
//    private List<String> reviews;
//    private List<Double> ratings;
}

package com.digicompass.backend.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteGeometryDto {
    private Long id;
    private String name;
    private Object geojson;

}

package com.digicompass.backend.application.models.route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteGeometry {
    private Long id;
    private String name;
    private Object geojson;

}

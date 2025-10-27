package com.digicompass.backend.infrastucture.persistence.models;

import com.digicompass.backend.presentation.controller.dto.RatingDto;
import com.digicompass.backend.presentation.controller.dto.ReviewDto;
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
    private List<Review> reviews;
    private List<Rating> ratings;
}

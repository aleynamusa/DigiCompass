package com.digicompass.backend.controller.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RouteFilterRequestDto {
    @Pattern(regexp = "hiking|cycling|walking|running", message = "Invalid route type")
    private String type;

    @Pattern(regexp = "EASY|MEDIUM|HARD", message = "Invalid difficulty")
    private String difficulty;

    @Min(value = 0, message = "Distance must be positive")
    private Float minDistance;

    @Min(value = 5, message = "Distance must be positive")
    private Float maxDistance;
}

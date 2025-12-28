package com.digicompass.backend.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PointDto {
    @NotBlank(message = "Latitude cannot be blank")
    @NotNull(message = "Latitude cannot be null")
    private double lat;

    @NotBlank(message = "Longitude cannot be blank")
    @NotNull(message = "Longitude cannot be null")
    private double lng;

}

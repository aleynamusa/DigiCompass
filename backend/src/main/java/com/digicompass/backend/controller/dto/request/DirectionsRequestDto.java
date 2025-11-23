package com.digicompass.backend.controller.dto.request;

import lombok.Data;

@Data
public class DirectionsRequestDto {
    private String origin;
    private String destination;
    private String waypoints;
    private String mode;
}

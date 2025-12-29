package com.digicompass.backend.controller.dto;

import lombok.Data;

@Data
public class GeoLocationDto {
    private String name;
    private double latitude;
    private double longitude;
}

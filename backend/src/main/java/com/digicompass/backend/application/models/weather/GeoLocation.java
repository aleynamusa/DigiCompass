package com.digicompass.backend.application.models.weather;

import lombok.Data;

@Data
public class GeoLocation {
    private String name;
    private double latitude;
    private double longitude;
}


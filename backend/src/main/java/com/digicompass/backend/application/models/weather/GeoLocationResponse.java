package com.digicompass.backend.application.models.weather;

import lombok.Data;

import java.util.List;

@Data
public class GeoLocationResponse {
    private List<GeoLocation> results;


}

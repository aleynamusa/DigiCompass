package com.digicompass.backend.controller.dto;

import com.digicompass.backend.application.models.weather.GeoLocation;
import lombok.Data;

import java.util.List;

@Data
public class GeoLocationResponseDto {
    private List<GeoLocationDto> results;
}

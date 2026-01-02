package com.digicompass.backend.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class GeoLocationResponseDto {
    private List<GeoLocationDto> results;
}

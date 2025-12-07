package com.digicompass.backend.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class GeoJsonDto {
    private String type;
    private List<List<Double>> coordinates;
}

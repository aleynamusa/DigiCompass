package com.digicompass.backend.application.models;

import lombok.Data;

import java.util.List;
@Data
public class GeoJson {
    private String type;
    private List<List<Double>> coordinates;
}

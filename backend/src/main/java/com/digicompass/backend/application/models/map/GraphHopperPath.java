package com.digicompass.backend.application.models.map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GraphHopperPath {
    private double distance;
    private long time;
    private String points;
}

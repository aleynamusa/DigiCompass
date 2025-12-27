package com.digicompass.backend.application.models.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphHopperPath {
    private double distance;
    private long time;
    private String points;
}

package com.digicompass.backend.infrastructure.objects.graph;

import lombok.Data;

@Data
public class GraphHopperPathObject {
    private double distance;
    private long time;
    private String points;
}

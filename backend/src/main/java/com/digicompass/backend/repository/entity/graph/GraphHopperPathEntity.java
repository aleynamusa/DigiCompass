package com.digicompass.backend.repository.entity.graph;

import lombok.Data;

@Data
public class GraphHopperPathEntity {
    private double distance;
    private long time;
    private String points;
}

package com.digicompass.backend.repository.entity.graph;

import lombok.Data;

import java.util.List;

@Data
public class GraphHopperEntity {
    private List<GraphHopperPathEntity> paths;
}

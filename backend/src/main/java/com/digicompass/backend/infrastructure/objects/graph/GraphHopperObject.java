package com.digicompass.backend.infrastructure.objects.graph;

import lombok.Data;

import java.util.List;

@Data
public class GraphHopperObject{
    private List<GraphHopperPathObject> paths;
}

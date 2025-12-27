package com.digicompass.backend.application.models.map;

import lombok.Data;


import java.util.List;

@Data
public class GraphHopper {
    private List<GraphHopperPath> paths;
}

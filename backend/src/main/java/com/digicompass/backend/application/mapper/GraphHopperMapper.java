package com.digicompass.backend.application.mapper;

import com.digicompass.backend.application.models.map.GraphHopperPath;

import com.digicompass.backend.infrastructure.objects.graph.GraphHopperPathObject;
import com.digicompass.backend.repository.entity.graph.GraphHopperPathEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GraphHopperMapper {
    GraphHopperPath getGraphHopperPath(GraphHopperPathEntity graphHopperPath);
    GraphHopperPath getGraphObject(GraphHopperPathObject graphHopperPath);
}

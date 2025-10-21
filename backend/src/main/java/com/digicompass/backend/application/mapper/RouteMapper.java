package com.digicompass.backend.application.mapper;

import com.digicompass.backend.domain.models.Route;
import com.digicompass.backend.infrastucture.persistence.entity.RouteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(componentModel = "spring")
public interface RouteMapper {

    // Optional: direct access without injection if needed

    com.digicompass.backend.presentation.controller.mapper.RouteMapperController INSTANCE = Mappers.getMapper(com.digicompass.backend.presentation.controller.mapper.RouteMapperController.class);

    Route toDomain(RouteEntity model);
    RouteEntity toEntity(Route model);

    List<Route> toDomain(List<RouteEntity> models);
    List<RouteEntity> toEntity(List<Route> models);

}



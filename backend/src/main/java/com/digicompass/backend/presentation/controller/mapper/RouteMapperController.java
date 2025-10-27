package com.digicompass.backend.presentation.controller.mapper;

import com.digicompass.backend.domain.entity.RouteImageEntity;
import com.digicompass.backend.infrastucture.persistence.models.Route;
import com.digicompass.backend.infrastucture.persistence.models.RouteGeometry;
import com.digicompass.backend.presentation.controller.dto.RouteDto;
import com.digicompass.backend.presentation.controller.dto.RouteGeometryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = RatingMapperController.class)
public interface RouteMapperController {

    RouteGeometryDto toControllerGeometry(RouteGeometry model);
    RouteDto toControllerRoute(Route model);

    List<RouteGeometryDto> toControllerGeometry(List<RouteGeometry> model);
    List<RouteDto> toControllerRoute(List<Route> model);

    default List<String> map(List<RouteImageEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream()
                .map(RouteImageEntity::getImageUrl) // or whatever your image field is
                .toList();
    }
}

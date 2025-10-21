package com.digicompass.backend.presentation.controller.mapper;

import com.digicompass.backend.domain.models.Route;
import com.digicompass.backend.domain.models.RouteGeometry;
import com.digicompass.backend.presentation.controller.dto.RouteDto;
import com.digicompass.backend.presentation.controller.dto.RouteGeometryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RouteMapperController {

    // Optional: direct access without injection if needed

    com.digicompass.backend.presentation.controller.mapper.RouteMapperController INSTANCE = Mappers.getMapper(com.digicompass.backend.presentation.controller.mapper.RouteMapperController.class);

    RouteGeometryDto toControllerGeometry(RouteGeometry model);
    RouteDto toControllerRoute(Route model);

    List<RouteGeometryDto> toControllerGeometry(List<RouteGeometry> model);
    List<RouteDto> toControllerRoute(List<Route> model);

}

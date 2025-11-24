package com.digicompass.backend.controller.mapper;

import com.digicompass.backend.application.models.Rating;
import com.digicompass.backend.controller.dto.RatingDto;
import com.digicompass.backend.repository.entity.RouteImageEntity;
import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.application.models.RouteGeometry;
import com.digicompass.backend.controller.dto.RouteDto;
import com.digicompass.backend.controller.dto.RouteGeometryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {RatingMapperController.class, ReviewMapperController.class})
public interface RouteMapperController {


    RouteGeometryDto toControllerGeometry(RouteGeometry model);

    @Mapping(target = "userId.id", source="userId.id")
    RouteDto toControllerRoute(Route model);



    List<RouteGeometryDto> toControllerGeometry(List<RouteGeometry> model);
    List<RouteDto> toControllerRoute(List<Route> model);

}

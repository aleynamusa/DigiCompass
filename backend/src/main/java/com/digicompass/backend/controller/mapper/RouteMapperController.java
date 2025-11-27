package com.digicompass.backend.controller.mapper;

import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.application.models.RouteGeometry;
import com.digicompass.backend.controller.dto.RouteDto;
import com.digicompass.backend.controller.dto.RouteGeometryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring", uses = {RatingMapperController.class, ReviewMapperController.class})
public interface RouteMapperController {


    RouteGeometryDto toControllerGeometry(RouteGeometry model);

    @Mapping(target = "createdByUserId.id", source="createdByUserId.id")
    RouteDto toControllerRoute(Route model);


    @Mapping(target = "createdByUserId.id", source="createdByUserId.id")
    List<RouteDto> toControllerRoute(List<Route> model);

}

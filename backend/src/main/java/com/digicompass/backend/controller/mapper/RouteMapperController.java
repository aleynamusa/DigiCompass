package com.digicompass.backend.controller.mapper;

import com.digicompass.backend.application.models.GeoJson;
import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.application.models.RouteGeometry;
import com.digicompass.backend.controller.dto.GeoJsonDto;
import com.digicompass.backend.controller.dto.RouteDto;
import com.digicompass.backend.controller.dto.RouteGeometryDto;
import com.digicompass.backend.controller.dto.request.RouteRequestDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring", uses = {RatingMapperController.class, ReviewMapperController.class})
public interface RouteMapperController {


    RouteGeometryDto toControllerGeometry(RouteGeometry model);


    @Mapping(target = "createdByUserId.id", source="createdByUserId.id")
    RouteDto toControllerRoute(Route model);

    @InheritInverseConfiguration
    Route toDomain(RouteRequestDto dto);


    @Mapping(target = "createdByUserId.id", source="createdByUserId.id")
    List<RouteDto> toControllerRoute(List<Route> model);

    GeoJson toDomainJson(GeoJsonDto dto);

}

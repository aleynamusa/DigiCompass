package com.digicompass.backend.presentation.controller.mapper;

import com.digicompass.backend.application.models.Rating;
import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.application.models.RouteGeometry;
import com.digicompass.backend.presentation.controller.dto.RatingDto;
import com.digicompass.backend.presentation.controller.dto.RouteDto;
import com.digicompass.backend.presentation.controller.dto.RouteGeometryDto;
import org.mapstruct.Mapper;

import java.util.List;
@Mapper(componentModel = "spring")
public interface RatingMapperController {

    RatingDto toControllerGeometry(Rating model);

    List<RouteGeometryDto> toControllerGeometry(List<RouteGeometry> model);
    List<RouteDto> toControllerRoute(List<Route> model);
}

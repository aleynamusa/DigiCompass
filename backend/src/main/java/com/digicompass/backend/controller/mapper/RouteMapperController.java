package com.digicompass.backend.controller.mapper;

import com.digicompass.backend.application.models.route.Route;
import com.digicompass.backend.application.models.route.RouteGeometry;
import com.digicompass.backend.controller.dto.RouteDto;
import com.digicompass.backend.controller.dto.RouteGeometryDto;
import com.digicompass.backend.controller.dto.request.RouteRequestDto;
import jakarta.validation.Valid;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {RatingMapperController.class, ReviewMapperController.class, MultipartFileMapper.class})
public interface RouteMapperController {

    RouteGeometryDto toControllerGeometry(RouteGeometry model);

    @Mapping(target = "createdByUserId.id", source="createdByUserId.id")
    RouteDto toControllerRoute(Route model);

    @Mapping(target = "routeType", source = "routeType")
    @Mapping(target = "difficulty", source = "difficulty")
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "createdByUserId", ignore = true)
    @Mapping(target = "routeGeometry", expression = "java(parseGeoJsonToGeometry(dto.getGeometry()))")
    Route toDomain(@Valid RouteRequestDto dto);

    @Mapping(target = "images", ignore = true)
    RouteDto toControllerRouteDto(@Valid RouteRequestDto dto);


    @Mapping(target = "createdByUserId.id", source="createdByUserId.id")
    List<RouteDto> toControllerRoute(List<Route> model);


    default Geometry parseGeoJsonToGeometry(String geometryJson) {
        try {
            if (geometryJson == null || geometryJson.isBlank()) return null;

            // If it arrives quoted (double-stringified), unwrap once
            String g = geometryJson;
            if (g.startsWith("\"") && g.endsWith("\"")) {
                g = g.substring(1, g.length() - 1).replace("\\\"", "\"");
            }

            return new GeoJsonReader().read(g);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid geometry GeoJSON", e);
        }
    }
}

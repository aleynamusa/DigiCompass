package com.digicompass.backend.controller.mapper;

import com.digicompass.backend.application.models.map.Point;
import com.digicompass.backend.application.models.map.RouteMap;
import com.digicompass.backend.controller.dto.PointDto;
import com.digicompass.backend.controller.dto.response.RouteResponseMapDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapMapper{
    List<Point> toModel(List<PointDto> dto);
    RouteMap toRouteMap(RouteResponseMapDto dto);
    RouteResponseMapDto toRouteResponseDto(RouteMap model);
}
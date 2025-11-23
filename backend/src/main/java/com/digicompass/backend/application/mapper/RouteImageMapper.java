package com.digicompass.backend.application.mapper;

import com.digicompass.backend.application.models.RouteImage;
import com.digicompass.backend.repository.entity.RouteEntity;
import com.digicompass.backend.repository.entity.RouteImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RouteImageMapper {


    @Mapping(source = "presignedUrl", target = "imageUrl")
    @Mapping(source = "route", target = "route")
    RouteImageEntity toEntity(RouteImage model);


    @Mapping(source = "imageUrl", target = "presignedUrl")
    @Mapping(source = "route.id", target = "route")
    RouteImage toModel(RouteImageEntity entity);

    default RouteEntity map(Long id) {
        if (id == null) return null;
        RouteEntity route = new RouteEntity();
        route.setId(id);
        return route;
    }

    default Long map(RouteEntity route) {
        if (route == null) return null;
        return route.getId();
    }
}

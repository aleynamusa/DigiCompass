package com.digicompass.backend.application.mapper;

import com.digicompass.backend.application.models.route.Route;
import com.digicompass.backend.repository.entity.RouteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface CreateRouteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    RouteEntity toEntity(Route route);
}

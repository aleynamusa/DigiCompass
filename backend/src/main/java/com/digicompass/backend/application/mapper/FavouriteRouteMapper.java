package com.digicompass.backend.application.mapper;

import com.digicompass.backend.application.models.FavouriteRoute;
import com.digicompass.backend.repository.entity.FavouriteRouteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, RouteMapper.class})
public interface FavouriteRouteMapper {

    @Mapping(target = "id.userId", source = "user.id")
    @Mapping(target = "id.routeId", source = "route.id")
    FavouriteRouteEntity toEntity(FavouriteRoute favouriteRoute);

    FavouriteRoute toDomain(FavouriteRouteEntity entity);

}

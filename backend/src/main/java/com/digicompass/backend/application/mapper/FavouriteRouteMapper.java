package com.digicompass.backend.application.mapper;

import com.digicompass.backend.application.models.FavouriteRoute;
import com.digicompass.backend.repository.entity.FavouriteRouteEntity;
import com.digicompass.backend.repository.entity.FavouriteRouteKey;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserMapper.class, RouteMapper.class})
public abstract class FavouriteRouteMapper {

    @Mapping(target = "id", ignore = true) // we set it manually
    public abstract FavouriteRouteEntity toEntity(FavouriteRoute favouriteRoute);

    public abstract FavouriteRoute toDomain(FavouriteRouteEntity entity);

    @AfterMapping
    protected void assignKey(FavouriteRoute favouriteRoute, @MappingTarget FavouriteRouteEntity entity) {
        entity.setId(new FavouriteRouteKey(
                favouriteRoute.getUser().getId(),
                favouriteRoute.getRoute().getId()
        ));
    }
}

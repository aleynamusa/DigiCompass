package com.digicompass.backend.application.mapper;

import com.digicompass.backend.application.models.FavouriteRoute;
import com.digicompass.backend.repository.entity.FavouriteRouteEntity;
import com.digicompass.backend.repository.entity.FavouriteRouteKey;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserMapper.class, RouteMapper.class})
public interface FavouriteRouteMapper {

//    id
//    @Mapping(target = "id", ignore = true) working vers
    @Mapping(target = "id.userId", source = "user.id")
    @Mapping(target = "id.routeId", source = "route.id")
    FavouriteRouteEntity toEntity(FavouriteRoute favouriteRoute);

    FavouriteRoute toDomain(FavouriteRouteEntity entity);

//    @AfterMapping
//    protected void assignKey(FavouriteRoute favouriteRoute, @MappingTarget FavouriteRouteEntity entity) {
//        entity.setId(new FavouriteRouteKey(
//                favouriteRoute.getUser().getId(),
//                favouriteRoute.getRoute().getId()
//        ));
//    }
}

package com.digicompass.backend.application.mapper;

import com.digicompass.backend.repository.entity.RatingEntity;
import com.digicompass.backend.application.models.Rating;
import com.digicompass.backend.repository.entity.RouteEntity;
import com.digicompass.backend.repository.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RouteMapper.class, UserMapper.class})
public interface RatingMapper {


    @Mapping(target = "userId",    source = "userId")
    @Mapping(target = "routeId",   source = "routeId.id")
    Rating toDomain(RatingEntity entity);

    @Mapping(target = "routeId.id", source = "routeId")
    @Mapping(target = "userId.id", source = "userId")
    RatingEntity toEntity(Rating review);


    List<Rating> toDomain(List<RatingEntity> entities);
    List<RatingEntity> toEntity(List<Rating> reviews);

    default Long map(UserEntity user) {
        return user != null ? user.getId() : null;
    }

    default Long map(RouteEntity route) {
        return route != null ? route.getId() : null;
    }
}

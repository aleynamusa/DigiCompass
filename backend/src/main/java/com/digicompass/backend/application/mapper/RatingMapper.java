package com.digicompass.backend.application.mapper;

import com.digicompass.backend.repository.entity.RatingEntity;
import com.digicompass.backend.application.models.route.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RouteMapper.class, UserMapper.class})
public interface RatingMapper {


    @Mapping(target = "userId.id",    source = "userId.id")
    @Mapping(target = "routeId",   source = "routeId.id")
    Rating toDomain(RatingEntity entity);

    @Mapping(target = "routeId.id", source = "routeId")
    @Mapping(target = "userId.id", source = "userId.id")
    RatingEntity toEntity(Rating review);


    List<Rating> toDomain(List<RatingEntity> entities);
    List<RatingEntity> toEntity(List<Rating> reviews);

}

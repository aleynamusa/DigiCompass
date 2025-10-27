package com.digicompass.backend.application.mapper;

import com.digicompass.backend.domain.entity.RatingEntity;
import com.digicompass.backend.domain.entity.ReviewEntity;
import com.digicompass.backend.infrastucture.persistence.models.Rating;
import com.digicompass.backend.infrastucture.persistence.models.Review;
import com.digicompass.backend.infrastucture.persistence.models.Route;
import com.digicompass.backend.infrastucture.persistence.models.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {RouteMapper.class, UserMapper.class})
public interface RatingMapper {

    @Mappings({
            @Mapping(target = "id",        source = "id"),
            @Mapping(target = "rating",    source = "rating"),
            @Mapping(target = "userId",    source = "userId"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "updatedAt", source = "updatedAt"),
            @Mapping(target = "routeId",   ignore = true)
    })
    Rating toDomain(RatingEntity entity);

    @InheritInverseConfiguration(name = "toDomain")
    @Mapping(target = "routeId", ignore = true)
    RatingEntity toEntity(Rating review);


    List<Rating> toDomain(List<RatingEntity> entities);
    List<RatingEntity> toEntity(List<Rating> reviews);
}

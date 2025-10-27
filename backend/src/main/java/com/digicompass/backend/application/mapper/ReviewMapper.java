package com.digicompass.backend.application.mapper;

import com.digicompass.backend.domain.entity.ReviewEntity;
import com.digicompass.backend.infrastucture.persistence.models.Review;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RouteMapper.class, UserMapper.class, RouteImageMapper.class})
public interface ReviewMapper {

    // DOMAIN <- ENTITY
    @Mappings({
            @Mapping(target = "id",        source = "id"),
            @Mapping(target = "review",    source = "review"),
            @Mapping(target = "userId",    source = "userId"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "updatedAt", source = "updatedAt"),
            @Mapping(target = "images",    source = "images"),
            @Mapping(target = "routeId",   ignore = true) // <-- drop the route from each review
    })
    Review toDomain(ReviewEntity entity);


    // ENTITY <- DOMAIN (inherit the above, and still ignore routeId)
    @InheritInverseConfiguration(name = "toDomain")
    @Mapping(target = "routeId", ignore = true)
    ReviewEntity toEntity(Review review);


    List<Review> toDomain(List<ReviewEntity> entities);
    List<ReviewEntity> toEntity(List<Review> reviews);
}

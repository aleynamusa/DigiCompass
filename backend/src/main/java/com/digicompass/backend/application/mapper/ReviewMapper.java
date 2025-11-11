package com.digicompass.backend.application.mapper;

import com.digicompass.backend.application.models.Review;
import com.digicompass.backend.domain.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

    @Mapping(source = "routeId.id", target = "routeId")
    @Mapping(target = "images", expression = "java(mapImages(entity.getImages()))")
    Review toDomain(ReviewEntity entity);

    @Mapping(source = "routeId", target = "routeId.id")
    @Mapping(target = "images", expression = "java(mapFromStrings(review.getImages()))")
    ReviewEntity toEntity(Review review);

    @Mapping(source = "routeId.id", target = "routeId")
    @Mapping(target = "images", expression = "java(mapImages(entity.getImages()))")
    List<Review> toDomain(List<ReviewEntity> entities);
    List<ReviewEntity> toEntity(List<Review> reviews);

    default List<String> mapImages(List<ReviewImageEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream()
                .map(ReviewImageEntity::getImageUrl)
                .collect(Collectors.toList());
    }

    default List<ReviewImageEntity> mapFromStrings(List<String> urls) {
        if (urls == null) return new ArrayList<>();
        return urls.stream().map(url -> {
            ReviewImageEntity img = new ReviewImageEntity();
            img.setImageUrl(url);
            return img;
        }).collect(Collectors.toList());
    }


    default RouteEntity mapRoute(Long routeId) {
        if (routeId == null) return null;
        RouteEntity route = new RouteEntity();
        route.setId(routeId);
        return route;
    }
}

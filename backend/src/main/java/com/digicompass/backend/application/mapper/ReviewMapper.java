package com.digicompass.backend.application.mapper;

import com.digicompass.backend.application.models.route.Review;
import com.digicompass.backend.repository.entity.*;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses={ReviewImageMapper.class})
public interface ReviewMapper {

    @AfterMapping
    default void linkImages(@MappingTarget ReviewEntity reviewEntity) {
        if (reviewEntity.getImages() == null) return;

        for (ReviewImageEntity image : reviewEntity.getImages()) {
            image.setReview(reviewEntity);
        }
    }

    @Mapping(source = "routeId.id", target = "routeId")
    @Mapping(source = "images", target = "images")
    Review toDomain(ReviewEntity entity);

    @Mapping(source = "routeId", target = "routeId.id")
    @Mapping(source = "images", target = "images")
    ReviewEntity toEntity(Review review);

    @Mapping(source = "routeId.id", target = "routeId")
    @Mapping(source = "images", target = "images")
    List<Review> toDomainList(List<ReviewEntity> entities);
    List<ReviewEntity> toEntity(List<Review> reviews);


    default List<String> mapEntitiesToUrls(List<ReviewImageEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream()
                .map(ReviewImageEntity::getImageUrl)
                .toList();
    }

    default List<ReviewImageEntity> mapUrlsToEntities(List<String> urls) {
        if (urls == null) return new ArrayList<>();

        List<ReviewImageEntity> entities = new ArrayList<>();
        for (String url : urls) {
            ReviewImageEntity e = new ReviewImageEntity();
            e.setImageUrl(url);
            entities.add(e);
        }
        return entities;
    }

}

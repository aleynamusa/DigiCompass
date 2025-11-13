package com.digicompass.backend.application.mapper;

import com.digicompass.backend.application.interfaces.S3Service;
import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.repository.entity.RatingEntity;
import com.digicompass.backend.repository.entity.ReviewEntity;
import com.digicompass.backend.repository.entity.RouteEntity;
import com.digicompass.backend.repository.entity.RouteImageEntity;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ReviewMapper.class})
public abstract class RouteMapper {

    @Autowired
    protected S3Service s3Service;
    @Mapping(source = "createdByUserId", target = "createdByUserId")
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    public abstract Route toDomain(RouteEntity entity);


    @Mapping(source = "createdByUserId", target = "createdByUserId")
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    public abstract RouteEntity toEntity(Route model);

    public abstract List<Route> toDomain(List<RouteEntity> entities);
    public abstract List<RouteEntity> toEntity(List<Route> models);


    @AfterMapping
    protected void mapImagesToDomain(RouteEntity entity, @MappingTarget Route route) {
        if (entity.getImages() != null && s3Service != null) {
            List<String> signedUrls = entity.getImages().stream()
                    .map(img -> s3Service.getPreSignedUrl(img.getImageUrl()))
                    .collect(Collectors.toList());
            route.setImages(signedUrls);
        }
    }

    @AfterMapping
    protected void mapImagesToEntity(Route model, @MappingTarget RouteEntity entity) {
        if (model.getImages() != null) {
            List<RouteImageEntity> imageEntities = model.getImages().stream()
                    .map(url -> {
                        RouteImageEntity imgEntity = new RouteImageEntity();
                        imgEntity.setImageUrl(url);
                        imgEntity.setRoute(entity);
                        return imgEntity;
                    })
                    .collect(Collectors.toList());
            entity.setImages(imageEntities);
        }
    }


    protected List<String> mapReviews(List<ReviewEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream()
                .map(ReviewEntity::getReview)
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    protected List<Double> mapRatings(List<RatingEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream()
                .map(RatingEntity::getRating)
                .collect(Collectors.toList());
    }

    protected List<RatingEntity> mapDoubles(List<Double> ratings) {
        if (ratings == null) return new ArrayList<>();
        return ratings.stream()
                .map(rating -> {
                    RatingEntity entity = new RatingEntity();
                    entity.setRating(rating);
                    return entity;
                })
                .collect(Collectors.toList());
    }
}

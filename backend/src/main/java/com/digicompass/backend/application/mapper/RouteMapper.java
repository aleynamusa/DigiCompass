package com.digicompass.backend.application.mapper;

import com.digicompass.backend.application.models.route.Route;
import com.digicompass.backend.infrastructure.interfaces.S3;
import com.digicompass.backend.repository.entity.RatingEntity;
import com.digicompass.backend.repository.entity.ReviewEntity;
import com.digicompass.backend.repository.entity.RouteEntity;
import com.digicompass.backend.repository.entity.RouteImageEntity;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ReviewMapper.class})
public abstract class RouteMapper {

    @Autowired
    protected S3 s3Service;

    @Mapping(source = "createdByUserId", target = "createdByUserId")
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "startLatitude", expression = "java(extractStartLat(entity))")
    @Mapping(target = "startLongitude", expression = "java(extractStartLon(entity))")
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
                    .toList();
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
                    .toList();
            entity.setImages(imageEntities);
        }
    }


    protected Double extractStartLat(RouteEntity entity) {
        if (entity == null || entity.getRouteGeometry() == null) return null;

        if (entity.getRouteGeometry() instanceof LineString line) {
            Point start = line.getStartPoint();
            return start != null ? start.getY() : null; // latitude
        }

        return null;
    }

    protected Double extractStartLon(RouteEntity entity) {
        if (entity == null || entity.getRouteGeometry() == null) return null;

        if (entity.getRouteGeometry() instanceof LineString line) {
            Point start = line.getStartPoint();
            return start != null ? start.getX() : null; // longitude
        }

        return null;
    }



    protected List<String> mapReviews(List<ReviewEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream()
                .map(ReviewEntity::getReview)
                .map(Object::toString)
                .toList();
    }

    protected List<Double> mapRatings(List<RatingEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream()
                .map(RatingEntity::getRating)
                .toList();
    }

    protected List<RatingEntity> mapDoubles(List<Double> ratings) {
        if (ratings == null) return new ArrayList<>();
        return ratings.stream()
                .map(rating -> {
                    RatingEntity entity = new RatingEntity();
                    entity.setRating(rating);
                    return entity;
                })
               .toList();
    }
}

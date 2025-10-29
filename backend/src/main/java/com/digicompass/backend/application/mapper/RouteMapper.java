package com.digicompass.backend.application.mapper;

import com.digicompass.backend.application.services.S3ServiceImpl;
import com.digicompass.backend.domain.entity.RouteEntity;
import com.digicompass.backend.domain.entity.RouteImageEntity;
import com.digicompass.backend.application.models.Route;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RouteMapper {

    private final S3ServiceImpl s3Service;
    private final UserMapper userMapper;


    public RouteMapper(S3ServiceImpl s3Service, UserMapper userMapper) {
        this.s3Service = s3Service;
        this.userMapper = userMapper;

    }

    public Route toDomain(RouteEntity entity) {
        if (entity == null) return null;

        Route route = new Route();
        route.setId(entity.getId());
        route.setName(entity.getName());
        route.setDescription(entity.getDescription());
        route.setRouteType(entity.getRouteType());
        route.setDifficulty(entity.getDifficulty());
        route.setDistance(entity.getDistance());
        route.setDuration(entity.getDuration());
        route.setCreatedAt(entity.getCreatedAt());
        route.setUpdatedAt(entity.getUpdatedAt());
        route.setCreatedByUserId(userMapper.toDomain(entity.getCreatedByUserId()));
        route.setRouteGeometry(entity.getRouteGeometry());


        // Convert image entities → signed URLs
        if (entity.getImages() != null) {
            List<String> signedUrls = entity.getImages().stream()
                    .map(img -> s3Service.getPreSignedUrl(img.getImageUrl()))
                    .collect(Collectors.toList());
            route.setImages(signedUrls);
        }

        return route;
    }


    public RouteEntity toEntity(Route model) {
        if (model == null) return null;

        RouteEntity entity = new RouteEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setDescription(model.getDescription());
        entity.setRouteType(model.getRouteType());
        entity.setDifficulty(model.getDifficulty());
        entity.setDistance(model.getDistance());
        entity.setDuration(model.getDuration());
        entity.setCreatedAt(model.getCreatedAt());
        entity.setUpdatedAt(model.getUpdatedAt());
        entity.setCreatedByUserId(userMapper.toEntity(model.getCreatedByUserId()));
        entity.setRouteGeometry(model.getRouteGeometry());

        // Convert image URLs → image entities
        if (model.getImages() != null) {
            List<RouteImageEntity> imageEntities = model.getImages().stream()
                    .map(url -> {
                        RouteImageEntity imgEntity = new RouteImageEntity();
                        imgEntity.setImageUrl(url);
                        imgEntity.setRoute(entity); // back reference
                        return imgEntity;
                    })
                    .collect(Collectors.toList());
            entity.setImages(imageEntities);
        }

        return entity;
    }

    public List<Route> toDomain(List<RouteEntity> entities) {
        return entities == null ? null :
                entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    public List<RouteEntity> toEntity(List<Route> models) {
        return models == null ? null :
                models.stream().map(this::toEntity).collect(Collectors.toList());
    }



}

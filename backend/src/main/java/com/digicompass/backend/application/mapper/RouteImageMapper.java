package com.digicompass.backend.application.mapper;

import com.digicompass.backend.domain.entity.RouteImageEntity;
import com.digicompass.backend.infrastucture.persistence.models.RouteImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RouteImageMapper {
    RouteImage toDomain(RouteImageEntity entity);
    RouteImageEntity toEntity(RouteImage model);
}

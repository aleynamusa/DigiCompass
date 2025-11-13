package com.digicompass.backend.application.mapper;

import com.digicompass.backend.application.models.Review;
import com.digicompass.backend.repository.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses={ReviewImageMapper.class})
public interface ReviewMapper {

    @Mapping(source = "routeId.id", target = "routeId")
    Review toDomain(ReviewEntity entity);

    @Mapping(source = "routeId", target = "routeId.id")
    ReviewEntity toEntity(Review review);

    @Mapping(source = "routeId.id", target = "routeId")
    List<Review> toDomain(List<ReviewEntity> entities);
    List<ReviewEntity> toEntity(List<Review> reviews);
}

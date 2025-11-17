package com.digicompass.backend.application.mapper;

import com.digicompass.backend.application.models.ReviewImage;
import com.digicompass.backend.repository.entity.ReviewImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ReviewMapper.class})
public interface ReviewImageMapper {

    @Mapping(source = "reviewId", target = "review.id")
    ReviewImageEntity toEntity(ReviewImage model);

    @Mapping(source = "review.id", target = "reviewId")
    ReviewImage toModel(ReviewImageEntity entity);


}

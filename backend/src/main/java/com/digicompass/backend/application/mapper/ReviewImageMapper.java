package com.digicompass.backend.application.mapper;

import com.digicompass.backend.application.models.ReviewImage;
import com.digicompass.backend.repository.entity.ReviewEntity;
import com.digicompass.backend.repository.entity.ReviewImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ReviewMapper.class})
public interface ReviewImageMapper {


    ReviewImageEntity toEntity(ReviewImage model);

    ReviewImage toModel(ReviewImageEntity entity);


}

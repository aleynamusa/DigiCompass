package com.digicompass.backend.application.mapper;

import com.digicompass.backend.repository.entity.ReviewImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewImageMapper {

    ReviewImageEntity toEntity(String image);


    String toModel(ReviewImageEntity entity);


}

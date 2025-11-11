package com.digicompass.backend.application.mapper;

import com.digicompass.backend.application.models.ReviewImage;
import com.digicompass.backend.domain.entity.ReviewEntity;
import com.digicompass.backend.domain.entity.ReviewImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewImageMapper {


    ReviewImageEntity toEntity(ReviewImage model);

    ReviewImage toModel(ReviewImageEntity entity);

    // ✅ Helpers (optional)
    default ReviewEntity map(Long id) {
        if (id == null) return null;
        ReviewEntity review = new ReviewEntity();
        review.setId(id);
        return review;
    }
}

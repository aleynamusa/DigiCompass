package com.digicompass.backend.controller.mapper;

import com.digicompass.backend.application.models.route.Review;
import com.digicompass.backend.controller.dto.request.ReviewRequestDto;
import com.digicompass.backend.controller.dto.response.ReviewResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapperController.class, MultipartFileMapper.class})
public interface ReviewMapperController {

    @Mapping(target = "images", ignore = true)
    ReviewRequestDto toDto(Review review);

    ReviewResponseDto toDtoResponse(Review review);

    List<ReviewResponseDto> toDtosResponse(List<Review> review);

    @Mapping(target = "images", ignore = true)
    Review toModel(ReviewRequestDto dto);

    Review toModel(ReviewResponseDto dto);

    List<ReviewRequestDto> toDtos(List<Review> reviews);
}

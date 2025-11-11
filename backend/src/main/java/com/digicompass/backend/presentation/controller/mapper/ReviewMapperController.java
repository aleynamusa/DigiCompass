package com.digicompass.backend.presentation.controller.mapper;

import com.digicompass.backend.application.models.Review;
import com.digicompass.backend.presentation.controller.dto.ReviewDto;
import com.digicompass.backend.presentation.controller.dto.UserDto;
import com.digicompass.backend.presentation.controller.dto.response.ReviewResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = UserMapperController.class)
public interface ReviewMapperController {

    @Mapping(target = "images", ignore = true)
    ReviewDto toDto(Review review);

    ReviewResponseDto toDtoResponse(Review review);

    List<ReviewResponseDto> toDtosResponse(List<Review> review);

    @Mapping(target = "images", ignore = true)
    Review toModel(ReviewDto dto);

    List<ReviewDto> toDtos(List<Review> reviews);

//    default UserDto map(Long userId) {
//        if (userId == null) return null;
//        UserDto userDto = new UserDto();
//        userDto.setId(userId);
//        return userDto;
//    }
//
//    default Long map(UserDto userDto) {
//        if (userDto == null) return null;
//        return userDto.getId();
//    }
}

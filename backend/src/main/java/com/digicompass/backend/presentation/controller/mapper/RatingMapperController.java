package com.digicompass.backend.presentation.controller.mapper;

import com.digicompass.backend.application.models.Rating;
import com.digicompass.backend.presentation.controller.dto.RatingDto;
import com.digicompass.backend.presentation.controller.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RatingMapperController {

    RatingDto toDto(Rating model);

    Rating toModel(RatingDto dto);

    List<RatingDto> toDto(List<Rating> model);
    List<Rating> toModel(List<RatingDto> dto);

    default UserDto map(Long userId) {
        if (userId == null) return null;
        UserDto dto = new UserDto();
        dto.setId(userId);
        return dto;
    }

    default Long map(UserDto userDto) {
        if (userDto == null) return null;
        return userDto.getId();
    }
}

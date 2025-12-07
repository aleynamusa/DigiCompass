package com.digicompass.backend.controller.mapper;

import com.digicompass.backend.application.models.Rating;
import com.digicompass.backend.controller.dto.RatingDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RatingMapperController {

    @Mapping(target = "userId.id", source="userId.id")
    RatingDto toDto(Rating model);

    @InheritInverseConfiguration
    Rating toModel(RatingDto dto);

    @Mapping(target = "userId.id", source="userId.id")
    List<RatingDto> toDto(List<Rating> model);

    @InheritInverseConfiguration
    List<Rating> toModel(List<RatingDto> dto);

}

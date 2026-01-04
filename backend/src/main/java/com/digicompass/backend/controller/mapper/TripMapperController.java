package com.digicompass.backend.controller.mapper;

import com.digicompass.backend.application.models.Trip;
import com.digicompass.backend.controller.dto.request.TripRequestDto;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TripMapperController {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Valid Trip toModel(@Valid TripRequestDto tripRequestDto);
}

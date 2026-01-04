package com.digicompass.backend.controller.mapper;

import com.digicompass.backend.application.models.Trip;
import com.digicompass.backend.controller.dto.request.TripRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TripMapperController {
    Trip toModel(TripRequestDto tripRequestDto);
}

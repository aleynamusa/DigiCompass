package com.digicompass.backend.presentation.controller.mapper;

import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.application.models.RouteGeometry;
import com.digicompass.backend.application.models.User;
import com.digicompass.backend.presentation.controller.dto.RouteDto;
import com.digicompass.backend.presentation.controller.dto.RouteGeometryDto;
import com.digicompass.backend.presentation.controller.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapperController {
    UserDto toController(User model);
    User toModel(UserDto dto);
}

package com.digicompass.backend.controller.mapper;

import com.digicompass.backend.application.models.User;
import com.digicompass.backend.controller.dto.UserDto;
import com.digicompass.backend.controller.dto.request.UserRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapperController {
    UserDto toController(User model);
    User toModel(UserDto dto);

    UserRequestDto toControllerRequest(User model);
    User toModel(UserRequestDto dto);
}

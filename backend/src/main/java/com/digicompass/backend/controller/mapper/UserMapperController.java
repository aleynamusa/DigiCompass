package com.digicompass.backend.controller.mapper;

import com.digicompass.backend.application.models.User;
import com.digicompass.backend.controller.dto.UserDto;
import com.digicompass.backend.controller.dto.request.UserRequestDto;
import com.digicompass.backend.controller.dto.response.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapperController {
    UserDto toController(User model);
    User toModel(UserDto dto);

    List<UserResponseDto> toController(List<User> models);

    UserRequestDto toControllerRequest(User model);
    User toModel(UserRequestDto dto);

    User toModel(UserResponseDto dto);
    UserResponseDto toControllerResponse(User model);
}

package com.digicompass.backend.controller.mapper;

import com.digicompass.backend.unit.models.User;
import com.digicompass.backend.controller.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapperController {
    UserDto toController(User model);
    User toModel(UserDto dto);
}

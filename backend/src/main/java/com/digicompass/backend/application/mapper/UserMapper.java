package com.digicompass.backend.application.mapper;

import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.application.models.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "role.id", source = "roleId")
    UserEntity toEntity(User user);

    @Mapping(target = "roleId", source = "role.id")
    User toDomain(UserEntity entity);



    @Mapping(target = "role.id", source = "roleId")
    List<User> toDomain(List<UserEntity> entities);

    @Mapping(target = "roleId", source = "role.id")
    List<UserEntity> toEntity(List<User> users);

}

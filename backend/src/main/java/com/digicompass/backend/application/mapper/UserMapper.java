package com.digicompass.backend.application.mapper;

import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.application.models.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {



    @Mapping(target = "role.id", source = "role_id")
    UserEntity toEntity(User user);

    @Mapping(target = "role_id", source = "role.id")
    User toDomain(UserEntity entity);


    @Mapping(target = "role.id", source = "role_id")
    List<User> toDomain(List<UserEntity> entities);

    @Mapping(target = "role_id", source = "role.id")

    List<UserEntity> toEntity(List<User> users);

}

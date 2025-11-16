package com.digicompass.backend.unit.mapper;

import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.unit.models.User;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {


    // Mapping methods
    UserEntity toEntity(User user);
    User toDomain(UserEntity entity);

    // Collection mappings
    List<User> toDomain(List<UserEntity> entities);
    List<UserEntity> toEntity(List<User> users);

}

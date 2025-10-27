package com.digicompass.backend.application.mapper;

import com.digicompass.backend.domain.entity.UserEntity;
import com.digicompass.backend.infrastucture.persistence.models.User;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

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

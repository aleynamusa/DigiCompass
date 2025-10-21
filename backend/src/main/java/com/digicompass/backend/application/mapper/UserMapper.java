package com.digicompass.backend.application.mapper;

import com.digicompass.backend.domain.models.User;
import com.digicompass.backend.infrastucture.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Optional: direct access without injection if needed
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Mapping methods
    UserEntity toEntity(User user);
    User toDomain(UserEntity entity);

    // Collection mappings
    List<User> toDomain(List<UserEntity> entities);
    List<UserEntity> toEntity(List<User> users);

}

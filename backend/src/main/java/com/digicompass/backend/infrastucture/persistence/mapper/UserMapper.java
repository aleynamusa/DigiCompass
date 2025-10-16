package com.digicompass.backend.infrastucture.persistence.mapper;

import com.digicompass.backend.domain.models.User;

import com.digicompass.backend.infrastucture.persistence.entity.UserEntity;
import com.github.dozermapper.core.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    @Autowired
    private static Mapper mapper;

    public UserMapper(Mapper mapper){
        this.mapper = mapper;
    }

    public static UserEntity toInfrastructure(User user) {
        return mapper.map(user, UserEntity.class);
    }

    public static User toDomain(UserEntity document) {
        return mapper.map(document, User.class);
    }
}

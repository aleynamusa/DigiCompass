package com.digicompass.backend.infrastucture.persistence.mapper;

import com.digicompass.backend.domain.models.Route;
import com.digicompass.backend.infrastucture.persistence.entity.RouteEntity;
import com.digicompass.backend.infrastucture.persistence.entity.UserEntity;
import com.github.dozermapper.core.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.stereotype.Component;

@Component
public class RouteMapper {
    @Autowired
    private static Mapper mapper;

    public static Route toDomain(RouteEntity route){
        return mapper.map(route, Route.class);
    }

    public static RouteEntity toInfrastructure(Route entity){
        return mapper.map(entity, RouteEntity.class);
    }
}

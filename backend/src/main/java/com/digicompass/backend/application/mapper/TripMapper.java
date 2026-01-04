package com.digicompass.backend.application.mapper;

import com.digicompass.backend.application.models.Trip;
import com.digicompass.backend.repository.entity.TripEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RouteMapper.class, UserMapper.class})
public interface TripMapper {
    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "route.id", source = "routeId")
    TripEntity ToEntity(Trip trip);
}

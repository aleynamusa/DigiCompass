package com.digicompass.backend.infrastucture.persistence.repository;

import com.digicompass.backend.domain.models.Route;
import com.digicompass.backend.domain.repositories.RouteJpaRepository;
import com.digicompass.backend.infrastucture.persistence.mapper.RouteMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RouteRepositoryImpl implements RouteInterface {


    private final RouteJpaRepository jpaRepository;

    public RouteRepositoryImpl(RouteJpaRepository repo){
        jpaRepository = repo;
    }

    @Override
    public List<Route> getAllRoutes() {
        return jpaRepository.findAll().stream()
                .map(RouteMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Route getRouteByName(String name) {
        return RouteMapper.toDomain(jpaRepository.findRouteEntityByName(name));
    }

    @Override
    public void deleteRoute(Route route) {

    }

    @Override
    public Route createRoute(Route route) {
        return null;
    }
}

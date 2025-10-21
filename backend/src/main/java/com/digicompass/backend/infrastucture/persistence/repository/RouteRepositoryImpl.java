package com.digicompass.backend.infrastucture.persistence.repository;

import com.digicompass.backend.domain.models.Route;
import com.digicompass.backend.domain.repositories.RouteJpaRepository;
import com.digicompass.backend.infrastucture.persistence.entity.RouteEntity;
import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.RouteInterface;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RouteRepositoryImpl implements RouteInterface {


    private final RouteJpaRepository jpaRepository;
    private final RouteMapper routeMapper;

    public RouteRepositoryImpl(RouteJpaRepository repo, RouteMapper mapper){
        jpaRepository = repo;
        this.routeMapper = mapper;
    }

//    @Override
//    public List<Route> getAllRoutes() {
//        return jpaRepository.findAll().stream()
//                .map(RouteMapper::toDomain)
//                .collect(Collectors.toList());
//    }

    @Override
    public List<RouteEntity> getAllRoutes() {
        List<RouteEntity> entities = jpaRepository.findAll();

        return entities;
    }

    @Override
    public RouteEntity getRouteByName(String name) {
        return jpaRepository.findRouteEntityByName(name);
    }

    @Override
    public void deleteRoute(RouteEntity route) {

    }

    @Override
    public RouteEntity createRoute(RouteEntity route) {
        return null;
    }

    @Override
    public RouteEntity findById(long id) {
        return jpaRepository.findById(id).orElse(null);
    }

    @Override
    public List<RouteEntity> getRoutesByKeyword(String keyword) {
        return jpaRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<RouteEntity> getAllRoutesByType(String type) {
        return  jpaRepository.findAllByRouteTypeContainingIgnoreCase(type);
    }

    @Override
    public List<RouteEntity> getAllRoutesByDifficulty(String difficulty) {
        return jpaRepository.findAllByDifficultyContainingIgnoreCase(difficulty);
    }

    @Override
    public List<RouteEntity> getAllRoutesByDistance(float distance) {
        return jpaRepository.findAllByDistance(distance);
    }

}

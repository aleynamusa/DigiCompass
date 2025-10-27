package com.digicompass.backend.infrastucture.persistence.repository;

import com.digicompass.backend.domain.entity.RouteEntity;
import com.digicompass.backend.domain.repositories.RatingJpaRepository;
import com.digicompass.backend.domain.repositories.RouteJpaRepository;

import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.RatingInterface;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.RouteInterface;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RouteRepositoryImpl implements RouteInterface {


    private final RouteJpaRepository jpaRepository;

    public RouteRepositoryImpl(RouteJpaRepository repo){
        jpaRepository = repo;

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

    @Override
    public List<RouteEntity> filterAll(String type, String difficulty, Float distance) {
        System.out.println("Filter params => type=" + type + ", difficulty=" + difficulty + ", distance=" + distance);
        return jpaRepository.findFiltered(type, difficulty, distance);
    }



}

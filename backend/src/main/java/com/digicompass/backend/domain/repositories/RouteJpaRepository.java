package com.digicompass.backend.domain.repositories;

import com.digicompass.backend.infrastucture.persistence.entity.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteJpaRepository extends JpaRepository<RouteEntity, Long> {
    RouteEntity findRouteEntityByName(String name);
    List<RouteEntity> findByNameContainingIgnoreCase(String keyword);
    List<RouteEntity> findAllByDifficultyContainingIgnoreCase(String difficulty);
    List<RouteEntity> findAllByRouteTypeContainingIgnoreCase(String type);
    List<RouteEntity> findAllByDistance(float distance);
}

package com.digicompass.backend.domain.repositories;

import com.digicompass.backend.infrastucture.persistence.entity.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteJpaRepository extends JpaRepository<RouteEntity, Long> {
    RouteEntity findRouteEntityByName(String name);
}

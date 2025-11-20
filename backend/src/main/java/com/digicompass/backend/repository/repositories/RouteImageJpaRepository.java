package com.digicompass.backend.repository.repositories;

import com.digicompass.backend.repository.entity.RouteImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteImageJpaRepository extends JpaRepository<RouteImageEntity, Long> {
}

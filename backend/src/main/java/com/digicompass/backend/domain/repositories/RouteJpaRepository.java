package com.digicompass.backend.domain.repositories;


import com.digicompass.backend.domain.entity.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RouteJpaRepository extends JpaRepository<RouteEntity, Long> {
    RouteEntity findRouteEntityByName(String name);
    List<RouteEntity> findByNameContainingIgnoreCase(String keyword);
    List<RouteEntity> findAllByDifficultyContainingIgnoreCase(String difficulty);
    List<RouteEntity> findAllByRouteTypeContainingIgnoreCase(String type);
    List<RouteEntity> findAllByDistance(float distance);



    @Query("""
SELECT r FROM RouteEntity r
WHERE (LOWER(r.routeType) = LOWER(:type) OR :type IS NULL )
  AND (LOWER(r.difficulty) = LOWER(:difficulty) OR :difficulty IS NULL  )
  AND (
    :distance IS NULL OR
    (:distance < 10 AND r.distance <= 5) OR
    (:distance >= 10 AND :distance < 20 AND r.distance > 5 AND r.distance <= 15) OR
    (:distance >= 90 AND r.distance > 15)
  )
""")
    List<RouteEntity> findFiltered(
            @Param("type") String type,
            @Param("difficulty") String difficulty,
            @Param("distance") Float distance
    );

    @Query("select r.id from RouteEntity r")
    List<Long> getAllIds();




}

package com.digicompass.backend.repository.repositories;


import com.digicompass.backend.repository.entity.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RouteJpaRepository extends JpaRepository<RouteEntity, Long> {


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

    List<RouteEntity> getRouteEntitiesByName(String name);

//
//    @Query(
//            value = "SELECT ST_Length(route_geometry::geography) FROM routes WHERE id = :id",
//            nativeQuery = true
//    )
//    double getRouteDistance(@Param("id") Long id);
//
//
//    @Query(
//            value = "SELECT ST_AsText(ST_StartPoint(route_geometry)) FROM routes WHERE id = :id",
//            nativeQuery = true
//    )
//    String getStartLocation(@Param("id") Long id);

}

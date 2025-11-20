package com.digicompass.backend.repository.repositories;

import com.digicompass.backend.repository.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingJpaRepository extends JpaRepository<RatingEntity, Long> {


    @Query("SELECT AVG(r.rating) FROM RatingEntity r WHERE r.routeId.id = :routeId")
    Double getAvgRatingByRoute(@Param("routeId") Long routeId);


    @Query("""
        SELECT r FROM RatingEntity r
        JOIN FETCH r.userId u
        JOIN FETCH r.routeId ro
        WHERE ro.id = :routeId
    """)
    List<RatingEntity> getRatingsByRoute(@Param("routeId") Long routeId);

}

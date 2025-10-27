package com.digicompass.backend.domain.repositories;

import com.digicompass.backend.domain.entity.RatingEntity;
import com.digicompass.backend.domain.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

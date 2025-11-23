package com.digicompass.backend.repository.repositories;

import com.digicompass.backend.repository.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewJpaRepository extends JpaRepository<ReviewEntity, Long> {

    @Query("""
        SELECT r FROM ReviewEntity r
        JOIN FETCH r.userId u
        JOIN FETCH r.routeId ro
        WHERE ro.id = :routeId
    """)
    List<ReviewEntity> getReviewsByRoute(@Param("routeId") Long routeId);

}


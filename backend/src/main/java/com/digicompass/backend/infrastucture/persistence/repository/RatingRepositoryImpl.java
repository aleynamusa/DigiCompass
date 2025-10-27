package com.digicompass.backend.infrastucture.persistence.repository;

import com.digicompass.backend.domain.entity.RatingEntity;
import com.digicompass.backend.domain.entity.RouteEntity;
import com.digicompass.backend.domain.repositories.RatingJpaRepository;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.RatingInterface;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RatingRepositoryImpl implements RatingInterface {
    private final RatingJpaRepository ratingJpaRepository;

    public RatingRepositoryImpl(RatingJpaRepository ratingJpaRepository) {
        this.ratingJpaRepository = ratingJpaRepository;
    }

    @Override
    public Double getAllRatingByRouteId(Long id) {
        return ratingJpaRepository.getAvgRatingByRoute(id);
    }

    @Override
    public List<RatingEntity> getRatingsByRouteId(Long routeId) {
        return  ratingJpaRepository.getRatingsByRoute(routeId);
    }
}

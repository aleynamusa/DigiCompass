package com.digicompass.backend.infrastucture.persistence.repository;

import com.digicompass.backend.domain.entity.ReviewEntity;
import com.digicompass.backend.domain.repositories.ReviewJpaRepository;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.ReviewInterface;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReviewRepositoryImpl implements ReviewInterface {
    private final ReviewJpaRepository reviewJpaRepository;

    public ReviewRepositoryImpl(ReviewJpaRepository reviewJpaRepository) {
        this.reviewJpaRepository = reviewJpaRepository;
    }

    @Override
    public List<ReviewEntity> getReviewsByRoute(Long routeId) {
        return reviewJpaRepository.getReviewsByRoute(routeId);
    }
}

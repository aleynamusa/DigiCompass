package com.digicompass.backend.infrastucture.persistence.repository.interfaces;

import com.digicompass.backend.domain.entity.ReviewEntity;

import java.util.List;

public interface ReviewInterface {
    List<ReviewEntity> getReviewsByRoute(Long routeId);
    boolean deleteReviewByRoute(Long routeId);
    boolean createReview(ReviewEntity r);
    boolean updateReview(ReviewEntity r);
}

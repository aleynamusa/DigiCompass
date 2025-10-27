package com.digicompass.backend.infrastucture.persistence.repository.interfaces;

import com.digicompass.backend.domain.entity.ReviewEntity;
import com.digicompass.backend.infrastucture.persistence.models.Review;

import java.util.List;

public interface ReviewInterface {
    List<ReviewEntity> getReviewsByRoute(Long routeId);
}

package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.ReviewService;

import com.digicompass.backend.application.mapper.ReviewMapper;
import com.digicompass.backend.infrastucture.persistence.models.Review;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.ReviewInterface;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewInterface reviewRepo;
    private final ReviewMapper reviewMapper;
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ReviewServiceImpl.class.getName());

    public ReviewServiceImpl(ReviewInterface reviewRepo, ReviewMapper reviewMapper) {
        this.reviewRepo = reviewRepo;
        this.reviewMapper = reviewMapper;

    }
    @Override
    public List<Review> getReviewsByRoute(Long routeId) {

        List<Review> reviews = reviewMapper.toDomain(reviewRepo.getReviewsByRoute(routeId));

        LOGGER.log(Level.INFO, "Reviews fetched from DB: {0}", reviews.size());
        return reviews;
    }
}

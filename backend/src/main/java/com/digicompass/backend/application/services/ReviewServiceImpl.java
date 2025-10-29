package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.ReviewService;

import com.digicompass.backend.application.mapper.ReviewMapper;
import com.digicompass.backend.application.models.Review;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.ReviewInterface;

import com.digicompass.backend.infrastucture.persistence.repository.interfaces.RouteInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewInterface reviewRepo;
    private final ReviewMapper reviewMapper;
    private final RouteInterface routeRepo;
    private static final Logger LOGGER = Logger.getLogger(ReviewServiceImpl.class.getName());

    public ReviewServiceImpl(ReviewInterface reviewRepo, ReviewMapper reviewMapper, RouteInterface routeRepo) {
        this.reviewRepo = reviewRepo;
        this.reviewMapper = reviewMapper;
        this.routeRepo = routeRepo;
    }

    @Override
    public List<Review> getReviewsByRoute(Long routeId) {
        try {
            if (routeId == null || routeId <= 0 || !routeRepo.getAllIds().contains(routeId)) {
                LOGGER.log(Level.WARNING, "Invalid routeId provided: {0}", routeId);
                throw new IllegalArgumentException("Route ID must not be null or negative.");
            }

            var entities = reviewRepo.getReviewsByRoute(routeId);
            if (entities == null) {
                LOGGER.log(Level.WARNING, "No reviews found for routeId: {0}", routeId);
                return List.of();
            }

            List<Review> reviews = reviewMapper.toDomain(entities);

            LOGGER.log(Level.INFO, "Fetched {0} reviews for routeId {1}", new Object[]{reviews.size(), routeId});
            return reviews;

        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validation error fetching reviews: {0}", e.getMessage());
            throw e;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while fetching reviews for routeId {0}: {1}",
                    new Object[]{routeId, e.getMessage()});
            throw new RuntimeException("Unexpected error while fetching reviews.", e);
        }
    }
}

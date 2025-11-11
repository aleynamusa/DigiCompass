package com.digicompass.backend.infrastucture.persistence.repository;

import com.digicompass.backend.domain.entity.ReviewEntity;
import com.digicompass.backend.domain.repositories.ReviewImageJpaRepository;
import com.digicompass.backend.domain.repositories.ReviewJpaRepository;
import com.digicompass.backend.domain.repositories.RouteImageJpaRepository;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.ReviewInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class ReviewRepositoryImpl implements ReviewInterface {
    private final ReviewJpaRepository reviewJpaRepository;
    private final ReviewImageJpaRepository reviewImageJpaRepository;

    public ReviewRepositoryImpl(ReviewJpaRepository reviewJpaRepository, ReviewImageJpaRepository reviewImageJpaRepository) {
        this.reviewJpaRepository = reviewJpaRepository;
        this.reviewImageJpaRepository = reviewImageJpaRepository;
    }

    @Override
    public List<ReviewEntity> getReviewsByRoute(Long routeId) {
        log.debug("[INFRASTRUCTURE] Fetching reviews for routeId={}", routeId);
        try {
            List<ReviewEntity> reviews = reviewJpaRepository.getReviewsByRoute(routeId);
            log.info("[INFRASTRUCTURE] Retrieved {} reviews for routeId={}", reviews.size(), routeId);
            return reviews;
        } catch (DataAccessResourceFailureException e) {
            log.error("[INFRASTRUCTURE] Database connection failed while fetching reviews for routeId={}", routeId, e);
            throw e;
        } catch (RuntimeException e) {
            log.error("[INFRASTRUCTURE] Unexpected error fetching reviews for routeId={}", routeId, e);
            throw e;
        }
    }

    @Override
    public boolean deleteReviewByRoute(Long reviewId) {
        log.debug("[INFRASTRUCTURE] Attempting to delete review with id={}", reviewId);
        try {
            reviewJpaRepository.deleteById(reviewId);
            log.info("[INFRASTRUCTURE] Successfully deleted review with id={}", reviewId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            log.warn("[INFRASTRUCTURE] No review found with id={} to delete", reviewId);
            return false;
        } catch (DataAccessResourceFailureException e) {
            log.error("[INFRASTRUCTURE] Database access failure while deleting review with id={}", reviewId, e);
            throw e;
        } catch (RuntimeException e) {
            log.error("[INFRASTRUCTURE] Unexpected error deleting review with id={}", reviewId, e);
            return false;
        }
    }


    @Override
    public boolean createReview(ReviewEntity review) {
        log.debug("[INFRASTRUCTURE] Creating new review for routeId={} by userId={}",
                review.getRouteId(), review.getUserId());
        try {

            ReviewEntity savedReview = reviewJpaRepository.save(review);
            boolean exists = reviewJpaRepository.existsById(savedReview.getId());
            if (exists) {
                log.info("[INFRASTRUCTURE] Successfully created review with id={}", savedReview.getId());
            } else {
                log.warn("[INFRASTRUCTURE] Review creation may not have persisted for routeId={}", review.getRouteId());
            }
            return exists;
        } catch (OptimisticLockingFailureException e) {
            log.warn("[INFRASTRUCTURE] Concurrent modification detected while creating review for routeId={}",
                    review.getRouteId(), e);
            return false;
        } catch (RuntimeException e) {
            log.error("[INFRASTRUCTURE] Unexpected error creating review for routeId={}", review.getRouteId(), e);
            return false;
        }
    }

    @Override
    public boolean updateReview(ReviewEntity review) {
        log.debug("[INFRASTRUCTURE] Attempting to update review with id={}", review.getId());

        try {
            if (!reviewJpaRepository.existsById(review.getId())) {
                log.warn("[INFRASTRUCTURE] Cannot update non-existent review with id={}", review.getId());
                return false;
            }

            reviewJpaRepository.save(review); //acts as update too
            log.info("[INFRASTRUCTURE] Successfully updated review with id={}", review.getId());
            return true;

        } catch (OptimisticLockingFailureException e) {
            log.warn("[INFRASTRUCTURE] Concurrent modification detected while updating review with id={}", review.getId(), e);
            return false;
        } catch (DataAccessResourceFailureException e) {
            log.error("[INFRASTRUCTURE] Database access failure while updating review with id={}", review.getId(), e);
            throw e; // critical DB error — rethrow
        } catch (RuntimeException e) {
            log.error("[INFRASTRUCTURE] Unexpected error updating review with id={}", review.getId(), e);
            return false;
        }
    }

}

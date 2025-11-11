package com.digicompass.backend.infrastucture.persistence.repository;


import com.digicompass.backend.domain.entity.RatingEntity;
import com.digicompass.backend.domain.repositories.RatingJpaRepository;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.RatingInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Slf4j
@Repository
public class RatingRepositoryImpl implements RatingInterface {

    private final RatingJpaRepository ratingJpaRepository;

    public RatingRepositoryImpl(RatingJpaRepository ratingJpaRepository) {
        this.ratingJpaRepository = ratingJpaRepository;
    }

    @Override
    public Double getAllRatingByRouteId(Long id) {
        log.info("[INFRASTRUCTURE] Fetching average rating for route id={}", id);
        try {
            Double avgRating = ratingJpaRepository.getAvgRatingByRoute(id);
            log.debug("[INFRASTRUCTURE] Average rating for route {} = {}", id, avgRating);
            return avgRating;
        } catch (DataAccessResourceFailureException e) {
            log.error("[INFRASTRUCTURE] Database connection failed while fetching average rating for route id={}", id, e);
        } catch (Exception e) {
            log.error("[INFRASTRUCTURE] Unexpected error fetching average rating for route id={}", id, e);
        }
        return null;
    }

    @Override
    public List<RatingEntity> getRatingsByRouteId(Long routeId) {
        log.info("[INFRASTRUCTURE] Fetching all ratings for route id={}", routeId);
        try {
            List<RatingEntity> ratings = ratingJpaRepository.getRatingsByRoute(routeId);
            log.debug("[INFRASTRUCTURE] Found {} ratings for route id={}", ratings.size(), routeId);
            return ratings;
        } catch (DataAccessResourceFailureException e) {
            log.error("[INFRASTRUCTURE] Database connection failed while fetching ratings for route id={}", routeId, e);
        } catch (EmptyResultDataAccessException e) {
            log.warn("[INFRASTRUCTURE] No ratings found for route id={}", routeId);
        } catch (Exception e) {
            log.error("[INFRASTRUCTURE] Unexpected error fetching ratings for route id={}", routeId, e);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean addRating(RatingEntity entity) {
        log.info("[INFRASTRUCTURE] Adding new rating for routeId={} by userId={}",
                entity.getRouteId(), entity.getUserId());
        try {
            RatingEntity savedEntity = ratingJpaRepository.save(entity);
            boolean exists = ratingJpaRepository.existsById(savedEntity.getId());
            log.debug("[INFRASTRUCTURE] Rating saved successfully with id={}", savedEntity.getId());
            return exists;
        } catch (DataIntegrityViolationException e) {
            log.error("[INFRASTRUCTURE] Data integrity violation while adding rating for routeId={} — possible constraint issue",
                    entity.getRouteId(), e);
        } catch (TransactionSystemException e) {
            log.error("[INFRASTRUCTURE] Transaction system error while saving rating for routeId={}",
                    entity.getRouteId(), e);
        } catch (DataAccessResourceFailureException e) {
            log.error("[INFRASTRUCTURE] Database connection failed while saving rating for routeId={}",
                    entity.getRouteId(), e);
        } catch (Exception e) {
            log.error("[INFRASTRUCTURE] Unexpected error adding rating for routeId={}",
                    entity.getRouteId(), e);
        }
        return false;
    }

//    @SneakyThrows global exception handler
    @Override
    public boolean updateRating(RatingEntity dto) {
        log.info("[INFRASTRUCTURE] Updating rating with id={}", dto.getId());
        try {
            if (dto.getId() == null || !ratingJpaRepository.existsById(dto.getId())) {
                log.warn("[INFRASTRUCTURE] Cannot update rating — entity not found with id={}", dto.getId());
                return false;
            }
            ratingJpaRepository.save(dto);
            log.debug("[INFRASTRUCTURE] Rating updated successfully for id={}", dto.getId());
            return true;
        } catch (DataIntegrityViolationException e) {
            log.error("[INFRASTRUCTURE] Data integrity violation updating rating id={}", dto.getId(), e);
        } catch (TransactionSystemException e) {
            log.error("[INFRASTRUCTURE] Transaction error updating rating id={}", dto.getId(), e);
        } catch (DataAccessResourceFailureException e) {
            log.error("[INFRASTRUCTURE] Database connection failed while updating rating id={}", dto.getId(), e);
        } catch (Exception e) {
            log.error("[INFRASTRUCTURE] Unexpected error updating rating id={}", dto.getId(), e);
        }
        return false;
    }
}

package com.digicompass.backend.infrastucture.persistence.repository;

import com.digicompass.backend.domain.entity.RouteEntity;
import com.digicompass.backend.domain.repositories.RatingJpaRepository;
import com.digicompass.backend.domain.repositories.RouteJpaRepository;

import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.RatingInterface;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.RouteInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionSystemException;

import java.util.Collections;
import java.util.List;

@Slf4j
@Repository
public class RouteRepositoryImpl implements RouteInterface {

    private final RouteJpaRepository jpaRepository;

    public RouteRepositoryImpl(RouteJpaRepository repo) {
        this.jpaRepository = repo;
    }

    @Override
    public List<RouteEntity> getAllRoutes() {
        log.info("[INFRASTRUCTURE] Fetching all routes");
        try {
            List<RouteEntity> entities = jpaRepository.findAll();
            log.debug("[INFRASTRUCTURE] Found {} routes", entities.size());
            return entities;
        } catch (DataAccessResourceFailureException e) {
            log.error("[INFRASTRUCTURE] Database connection failed while fetching all routes", e);
        } catch (Exception e) {
            log.error("[INFRASTRUCTURE] Unexpected error fetching all routes", e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Long> getAllIds() {
        log.info("[INFRASTRUCTURE] Fetching all route IDs");
        try {
            List<Long> ids = jpaRepository.getAllIds();
            log.debug("[INFRASTRUCTURE] Found {} route IDs", ids.size());
            return ids;
        } catch (DataAccessResourceFailureException e) {
            log.error("[INFRASTRUCTURE] Database connection failed while fetching route IDs", e);
        } catch (Exception e) {
            log.error("[INFRASTRUCTURE] Unexpected error fetching route IDs", e);
        }
        return Collections.emptyList();
    }

    @Override
    public RouteEntity findById(long id) {
        log.info("[INFRASTRUCTURE] Fetching route by id={}", id);
        try {
            RouteEntity route = jpaRepository.findById(id).orElse(null);
            if (route == null) {
                log.warn("[INFRASTRUCTURE] Route not found for id={}", id);
            } else {
                log.debug("[INFRASTRUCTURE] Found route with id={}", id);
            }
            return route;
        } catch (DataAccessResourceFailureException e) {
            log.error("[INFRASTRUCTURE] Database connection failed while fetching route id={}", id, e);
        } catch (Exception e) {
            log.error("[INFRASTRUCTURE] Unexpected error fetching route id={}", id, e);
        }
        return null;
    }

    @Override
    public List<RouteEntity> filterAll(String type, String difficulty, Float distance) {
        log.info("[INFRASTRUCTURE] Filtering routes with params type={}, difficulty={}, distance={}",
                type, difficulty, distance);
        try {
            List<RouteEntity> filteredRoutes = jpaRepository.findFiltered(type, difficulty, distance);
            log.debug("[INFRASTRUCTURE] Found {} filtered routes", filteredRoutes.size());
            return filteredRoutes;
        } catch (DataIntegrityViolationException e) {
            log.error("[INFRASTRUCTURE] Invalid filter query parameters: type={}, difficulty={}, distance={}",
                    type, difficulty, distance, e);
        } catch (DataAccessResourceFailureException e) {
            log.error("[INFRASTRUCTURE] Database connection failed while filtering routes", e);
        } catch (TransactionSystemException e) {
            log.error("[INFRASTRUCTURE] Transaction system error while filtering routes", e);
        } catch (Exception e) {
            log.error("[INFRASTRUCTURE] Unexpected error filtering routes", e);
        }
        return Collections.emptyList();
    }
}
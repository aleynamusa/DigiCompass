package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.RatingService;
import com.digicompass.backend.application.mapper.RatingMapper;
import com.digicompass.backend.application.models.Rating;
import com.digicompass.backend.repository.entity.RatingEntity;
import com.digicompass.backend.repository.repositories.RatingJpaRepository;
import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RatingServiceImpl implements RatingService {


    private final RatingJpaRepository ratingRepo;
    private final RouteJpaRepository routeRepo;
    private final RatingMapper ratingMapper;

    public RatingServiceImpl(RatingJpaRepository ratingRepo,
                             RouteJpaRepository routeRepo,
                             RatingMapper ratingMapper) {
        this.ratingRepo = ratingRepo;
        this.routeRepo = routeRepo;
        this.ratingMapper = ratingMapper;
    }

    @Override
    public Double getRouteRating(Long id) {
        try {
            if (id == null || !routeRepo.getAllIds().contains(id)) {
                log.warn("Invalid route id: {}", id);
                throw new IllegalArgumentException(
                        String.format("The route id cannot be null or the route id %s could not be found.", id)
                );
            }

            Double rating = ratingRepo.getAvgRatingByRoute(id);
            log.info("The rating of route id {} has been recorded: {}", id, rating);
            return rating != null ? rating : 0.0;

        } catch (IllegalArgumentException e) {
            log.warn("Validation error while fetching route rating: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error calculating rating for route {0}: {1}",
                    new Object[]{id, e.getMessage()});
            throw new ArithmeticException("Unexpected error while calculating rating: " + e.getMessage());
        }
    }

    @Override
    public List<Rating> getRatingsByRouteId(Long routeId) {
        try {
            if (routeId == null || !routeRepo.getAllIds().contains(routeId)) {
                log.warn("Invalid route id: {}", routeId);
                throw new IllegalArgumentException(
                        String.format("The route id cannot be null or the route id %s could not be found.", routeId)
                );
            }

            List<Rating> ratings = ratingMapper.toDomain(ratingRepo.getRatingsByRoute(routeId));
            log.info("Route {} has {} ratings", routeId, ratings.size());
            return ratings;

        } catch (IllegalArgumentException e) {
            log.warn( "Validation error while fetching ratings for route: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error fetching ratings for route {}: {}",
                    routeId, e.getMessage());
            throw new ArithmeticException("Unexpected error while fetching route ratings: " + e.getMessage());
        }
    }

    @Override
    public boolean addRating(Rating rating) {
        try {
            RatingEntity response = ratingRepo.save(ratingMapper.toEntity(rating));
            if (response.getId() != null) {
                log.info("Added Rating {}", rating.getId());
                return true;
            }

            return false;
        }catch (IllegalArgumentException e) {
            log.warn("Validation error adding rating: {}", e.getMessage());
        }
        return false;
    }
}

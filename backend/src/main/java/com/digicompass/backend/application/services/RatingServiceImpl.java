package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.RatingService;
import com.digicompass.backend.application.mapper.RatingMapper;
import com.digicompass.backend.application.models.route.Rating;
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
    public String getRouteRating(Long id) {
        try {
            if (id == null || !routeRepo.getAllIds().contains(id)) {
                log.warn("[SERVICE] Invalid route id: {}", id);
                throw new IllegalArgumentException(
                        String.format("The route id cannot be null or the route id %s could not be found.", id)
                );
            }

            Double avg = ratingRepo.getAvgRatingByRoute(id);

            if (avg == null) {
                log.info("[SERVICE] No ratings found for route id {}. Returning 0.00", id);
                return "0.00";
            }

            String rating = String.format("%.2f", avg);
            log.info("[SERVICE] Rating for route id {}: {}", id, rating);

            return rating;

        } catch (IllegalArgumentException e) {
            log.warn("[SERVICE] Validation error while fetching route rating: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("[SERVICE] Unexpected error calculating rating for route {}: {}",
                    id, e.getMessage());
            throw new ArithmeticException("Unexpected error while calculating rating: " + e.getMessage());
        }
    }


    @Override
    public List<Rating> getRatingsByRouteId(Long routeId) {
        try {
            if (routeId == null || !routeRepo.getAllIds().contains(routeId)) {
                log.warn("[SERVICE] Invalid route id: {}", routeId);
                throw new IllegalArgumentException(
                        String.format("The route id cannot be null or the route id %s could not be found.", routeId)
                );
            }

            List<Rating> ratings = ratingMapper.toDomain(ratingRepo.getRatingsByRoute(routeId));
            log.info("[SERVICE] Route {} has {} ratings", routeId, ratings.size());
            return ratings;

        } catch (IllegalArgumentException e) {
            log.warn( "[SERVICE] Validation error while fetching ratings for route: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("[SERVICE] Unexpected error fetching ratings for route {}: {}",
                    routeId, e.getMessage());
            throw new ArithmeticException("Unexpected error while fetching route ratings: " + e.getMessage());
        }
    }

    @Override
    public Rating addRating(Rating rating) {
        try {
            Rating response = ratingMapper.toDomain(ratingRepo.save(ratingMapper.toEntity(rating)));
            if (response.getId() != null) {
                log.info("[SERVICE] Added Rating {}", rating.getId());
                return response;
            }else  {
                log.info("[SERVICE] Failed to save Rating {}", rating.getId());
            }

        }catch (NullPointerException e) {
            log.warn("[SERVICE] Validation error adding rating: {}", e.getMessage());
            throw new NullPointerException(e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteRating(Long ratingId) {
        log.info("[SERVICE] Deleting Rating {}", ratingId);
        try{
                if(ratingRepo.existsById(ratingId)) {
                ratingRepo.deleteById(ratingId);
                if(ratingRepo.existsById(ratingId)){
                    log.info("[SERVICE] Successfully deleted Rating {}", ratingId);
                }
                else{
                    log.info("[SERVICE] Failed to delete Rating {}", ratingId);
                }
                log.info("[SERVICE] Deleted Rating {}", ratingId);
            }
        }
        catch (Exception e){
            log.error("[SERVICE] Unexpected error deleting Rating {}: {}",
                    ratingId, e.getMessage());
            throw new ArithmeticException("Unexpected error deleting Rating: " + e.getMessage());
        }
    }

    @Override
    public Rating updateRating(Rating rating) {
        log.info("[SERVICE] Updating Rating {}", rating.getId());

        try{
            Rating newRating = ratingMapper.toDomain(ratingRepo.save(ratingMapper.toEntity(rating)));
            log.info("[SERVICE] Successfully updated Rating {}", rating.getId());
            return newRating;
        }
        catch (Exception e){
            log.error("[SERVICE] Unexpected error updating Rating {}: {}",
                    rating.getId(), e.getMessage());
            throw new ArithmeticException("Unexpected error updating Rating: " + e.getMessage());
        }
    }
}

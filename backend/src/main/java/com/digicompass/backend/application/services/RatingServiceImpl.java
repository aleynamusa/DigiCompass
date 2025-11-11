package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.RatingService;
import com.digicompass.backend.application.mapper.RatingMapper;
import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.application.models.Rating;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.RatingInterface;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.RouteInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class RatingServiceImpl implements RatingService {

    private static final Logger LOGGER = Logger.getLogger(RatingServiceImpl.class.getName());

    private final RatingInterface ratingRepo;
    private final RouteInterface routeRepo;
    private final RatingMapper ratingMapper;

    public RatingServiceImpl(RatingInterface ratingRepo,
                             RouteMapper routeMapper,
                             RouteInterface routeRepo,
                             RatingMapper ratingMapper) {
        this.ratingRepo = ratingRepo;
        this.routeRepo = routeRepo;
        this.ratingMapper = ratingMapper;
    }

    @Override
    public Double getRouteRating(Long id) {
        try {
            if (id == null || !routeRepo.getAllIds().contains(id)) {
                LOGGER.log(Level.WARNING, "Invalid route id: {0}", id);
                throw new IllegalArgumentException(
                        String.format("The route id cannot be null or the route id %s could not be found.", id)
                );
            }

            Double rating = ratingRepo.getAllRatingByRouteId(id);
            LOGGER.log(Level.INFO, "The rating of route id {0} has been recorded: {1}", new Object[]{id, rating});
            return rating != null ? rating : 0.0;

        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validation error while fetching route rating: {0}", e.getMessage());
            throw e;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error calculating rating for route {0}: {1}",
                    new Object[]{id, e.getMessage()});
            throw new ArithmeticException("Unexpected error while calculating rating: " + e.getMessage());
        }
    }

    @Override
    public List<Rating> getRatingsByRouteId(Long routeId) {
        try {
            if (routeId == null || !routeRepo.getAllIds().contains(routeId)) {
                LOGGER.log(Level.WARNING, "Invalid route id: {0}", routeId);
                throw new IllegalArgumentException(
                        String.format("The route id cannot be null or the route id %s could not be found.", routeId)
                );
            }

            List<Rating> ratings = ratingMapper.toDomain(ratingRepo.getRatingsByRouteId(routeId));
            LOGGER.log(Level.INFO, "Route {0} has {1} ratings", new Object[]{routeId, ratings.size()});
            return ratings;

        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validation error while fetching ratings for route: {0}", e.getMessage());
            throw e;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error fetching ratings for route {0}: {1}",
                    new Object[]{routeId, e.getMessage()});
            throw new ArithmeticException("Unexpected error while fetching route ratings: " + e.getMessage());
        }
    }

    @Override
    public boolean addRating(Rating rating) {
        try {
            boolean response = ratingRepo.addRating(ratingMapper.toEntity(rating));
            if (response) {
                LOGGER.log(Level.INFO, "Added Rating {0}", rating.getId());
            }

            return response;
        }catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validation error adding rating: {0}", e.getMessage());
        }
        return false;
    }
}

package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.RatingService;
import com.digicompass.backend.application.mapper.RatingMapper;
import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.infrastucture.persistence.models.Rating;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.RatingInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingServiceImpl implements RatingService {

    private final Logger LOGGER = LoggerFactory.getLogger(RatingServiceImpl.class);
    private final RatingInterface ratingRepo;
    private final RatingMapper ratingMapper;

    public RatingServiceImpl(RatingInterface ratingRepo, RouteMapper routeMapper, RatingMapper ratingMapper) {
        this.ratingRepo = ratingRepo;
        this.ratingMapper = ratingMapper;
    }


    @Override
    public Double getRouteRating(Long id) {
        try {
            Double rating = ratingRepo.getAllRatingByRouteId(id);
            LOGGER.info(String.format("The rating of this route id %s has been recorded: %s", id, rating));
            return rating != null ? rating : 0.0;
        }
        catch (Exception e) {
            LOGGER.error(String.format("The rating of this route id %s could not be calculated.", id));
            throw new ArithmeticException(e.getMessage());
        }

    }

    @Override
    public List<Rating> getRatingsByRouteId(Long routeId) {
        try{
            List<Rating> ratings = ratingMapper.toDomain(ratingRepo.getRatingsByRouteId(routeId));
            LOGGER.info(String.format("The route %s has %s ratings", routeId, ratings.size()));
            return ratings;
        }
        catch (Exception e){
            LOGGER.error(String.format("There was a problem when trying to fetch the ratings of route %s", routeId));
            throw new ArithmeticException(e.getMessage());
        }
    }
}

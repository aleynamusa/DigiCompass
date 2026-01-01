package com.digicompass.backend.application.interfaces;


import com.digicompass.backend.application.models.route.Rating;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RatingService {
    String getRouteRating(Long id);
    List<Rating> getRatingsByRouteId(Long routeId);
    Rating addRating(Rating rating);
    void deleteRating(Long ratingId);
    Rating updateRating(Rating rating);

}

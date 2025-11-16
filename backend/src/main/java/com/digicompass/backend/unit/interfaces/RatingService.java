package com.digicompass.backend.unit.interfaces;


import com.digicompass.backend.unit.models.Rating;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RatingService {
    Double getRouteRating(Long id);
    List<Rating> getRatingsByRouteId(Long routeId);
    boolean addRating(Rating rating);
}

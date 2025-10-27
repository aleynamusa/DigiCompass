package com.digicompass.backend.application.interfaces;


import com.digicompass.backend.infrastucture.persistence.models.Rating;
import com.digicompass.backend.infrastucture.persistence.models.Route;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RatingService {
    Double getRouteRating(Long id);
    List<Rating>  getRatingsByRouteId(Long routeId);
}

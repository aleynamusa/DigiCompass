package com.digicompass.backend.infrastucture.persistence.repository.interfaces;


import com.digicompass.backend.domain.entity.RatingEntity;
import com.digicompass.backend.presentation.controller.dto.RatingDto;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;


public interface RatingInterface {
    Double getAllRatingByRouteId(Long id);
    List<RatingEntity> getRatingsByRouteId(Long routeId);
    boolean  addRating(RatingEntity entity);
    boolean updateRating(RatingEntity entity);
}




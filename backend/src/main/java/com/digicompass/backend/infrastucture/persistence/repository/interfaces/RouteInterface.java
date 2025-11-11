package com.digicompass.backend.infrastucture.persistence.repository.interfaces;


import com.digicompass.backend.domain.entity.RouteEntity;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface RouteInterface {
    List<RouteEntity> getAllRoutes();
    RouteEntity findById(long id);
    List<RouteEntity> filterAll(String type, String difficulty, Float distance);
    List<Long> getAllIds();
}

package com.digicompass.backend.infrastucture.persistence.repository.interfaces;

import com.digicompass.backend.domain.entity.RouteImageEntity;

import java.util.List;

public interface RouteImageInterface {
    boolean saveImage(RouteImageEntity routeImage);
}

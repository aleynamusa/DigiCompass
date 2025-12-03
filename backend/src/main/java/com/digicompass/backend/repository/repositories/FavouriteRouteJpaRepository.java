package com.digicompass.backend.repository.repositories;

import com.digicompass.backend.repository.entity.FavouriteRouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavouriteRouteJpaRepository extends JpaRepository<FavouriteRouteEntity, Long> {
}

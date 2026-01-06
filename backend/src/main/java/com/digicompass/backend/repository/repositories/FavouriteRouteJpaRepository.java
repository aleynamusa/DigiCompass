package com.digicompass.backend.repository.repositories;

import com.digicompass.backend.repository.entity.FavouriteRouteEntity;
import com.digicompass.backend.repository.entity.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavouriteRouteJpaRepository extends JpaRepository<FavouriteRouteEntity, Long> {
    boolean existsByIdUserIdAndIdRouteId(Long userId, Long routeId);

    @Query("SELECT fr.route FROM FavouriteRouteEntity fr WHERE fr.user.id = :userId")
    List<RouteEntity> findAllLikedRoutesByUserId(Long userId);
}
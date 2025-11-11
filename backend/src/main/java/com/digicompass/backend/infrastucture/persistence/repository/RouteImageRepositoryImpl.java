package com.digicompass.backend.infrastucture.persistence.repository;

import com.digicompass.backend.domain.entity.RouteEntity;
import com.digicompass.backend.domain.entity.RouteImageEntity;
import com.digicompass.backend.domain.repositories.RouteImageJpaRepository;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.RouteImageInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
public class RouteImageRepositoryImpl implements RouteImageInterface {

    private final RouteImageJpaRepository routeImageJpaRepository;

    public RouteImageRepositoryImpl(RouteImageJpaRepository routeImageJpaRepository) {
        this.routeImageJpaRepository = routeImageJpaRepository;
    }

    @Override
    public boolean saveImage(RouteImageEntity routeImage) {
        if (routeImage == null) {
            return false;
        }

        try {
            routeImageJpaRepository.save(routeImage);

            boolean allExist = routeImageJpaRepository.existsById(routeImage.getId());

            return allExist;
        } catch (OptimisticLockingFailureException e) {

            log.warn(
                    "[INFRASTRUCTURE] Concurrent modification detected while saving images for routeId={}",
                    routeImage.getRoute().getId(),
                    e
            );
            return false;
        } catch (RuntimeException e) {
            log.error(
                    "[INFRASTRUCTURE] Unexpected error saving images for routeId={}",
                    routeImage.getRoute().getId(),
                    e
            );
            return false;
        }
    }
}

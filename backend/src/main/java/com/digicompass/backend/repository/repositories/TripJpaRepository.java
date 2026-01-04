package com.digicompass.backend.repository.repositories;

import com.digicompass.backend.repository.entity.TripEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripJpaRepository extends JpaRepository<TripEntity, Long> {
}

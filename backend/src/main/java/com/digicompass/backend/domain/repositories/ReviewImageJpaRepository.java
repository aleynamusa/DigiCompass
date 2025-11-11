package com.digicompass.backend.domain.repositories;

import com.digicompass.backend.domain.entity.ReviewImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewImageJpaRepository extends JpaRepository<ReviewImageEntity, Long>
{
}

package com.digicompass.backend.repository.repositories;

import com.digicompass.backend.repository.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleJpaRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByRole(String role);
}

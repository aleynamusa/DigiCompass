package com.digicompass.backend.domain.repositories;

import com.digicompass.backend.infrastucture.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findUserDocumentByUsername(String username);
    UserEntity findByEmail(String email);
}

package com.digicompass.backend.domain.repositories;


import com.digicompass.backend.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findUserDocumentByUsername(String username);
    UserEntity findByEmail(String email);
}

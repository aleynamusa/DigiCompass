package com.digicompass.backend.infrastucture.persistence.repository.interfaces;

import com.digicompass.backend.domain.models.User;
import com.digicompass.backend.infrastucture.persistence.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserInterface {
    UserEntity save(UserEntity user);
    Optional<UserEntity> findById(Long id);
    List<UserEntity> findAll();
    void delete(UserEntity user);
    UserEntity findByUsername(String username);
    UserEntity findByEmail(String email);
}

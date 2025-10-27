package com.digicompass.backend.infrastucture.persistence.repository;

import com.digicompass.backend.domain.entity.UserEntity;
import com.digicompass.backend.domain.repositories.UserJpaRepository;

import com.digicompass.backend.infrastucture.persistence.repository.interfaces.UserInterface;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserInterface {


    private final UserJpaRepository jpaRepository;

    public UserRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public UserEntity save(UserEntity user) {
        UserEntity saved = jpaRepository.save(user);
        return saved;
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<UserEntity> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void delete(UserEntity user) {
        UserEntity entity = user; // map to entity first
        jpaRepository.delete(entity);
    }

    @Override
    public UserEntity findByUsername(String username) {
        UserEntity entity = jpaRepository.findUserDocumentByUsername(username);
        return entity;
    }

    @Override
    public UserEntity findByEmail(String email) {
        UserEntity entity = jpaRepository.findByEmail(email);
        if (entity == null) {
            return null;
        }
        return entity;
    }
}

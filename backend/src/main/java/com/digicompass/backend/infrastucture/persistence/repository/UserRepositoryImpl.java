package com.digicompass.backend.infrastucture.persistence.repository;

import com.digicompass.backend.domain.models.User;
import com.digicompass.backend.domain.repositories.UserJpaRepository;
import com.digicompass.backend.infrastucture.persistence.entity.UserEntity;
import com.digicompass.backend.infrastucture.persistence.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserInterface {


    private final UserJpaRepository jpaRepository;

    public UserRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserEntity saved = jpaRepository.save(UserMapper.toInfrastructure(user));
        return UserMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id)
                .map(UserMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(User user) {
        UserEntity entity = UserMapper.toInfrastructure(user); // map to entity first
        jpaRepository.delete(entity);
    }

    @Override
    public User findByUsername(String username) {
        UserEntity entity = jpaRepository.findUserDocumentByUsername(username);
        return UserMapper.toDomain(entity);
    }

    @Override
    public User findByEmail(String email) {
        UserEntity entity = jpaRepository.findByEmail(email);
        if (entity == null) {
            return null;
        }
        return UserMapper.toDomain(entity);
    }
}

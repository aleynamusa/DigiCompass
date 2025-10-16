package com.digicompass.backend.infrastucture.persistence.repository;

import com.digicompass.backend.domain.models.User;

import java.util.List;
import java.util.Optional;

public interface UserInterface {
    User save(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    void delete(User user);
    User findByUsername(String username);
    User findByEmail(String email);
}

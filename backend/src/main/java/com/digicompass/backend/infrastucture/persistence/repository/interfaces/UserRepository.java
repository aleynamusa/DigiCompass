package com.digicompass.backend.infrastucture.persistence.repository.interfaces;

import com.digicompass.backend.domain.models.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository {

    User save(User user);
    Optional<User> findById(String id);
    List<User> findAll();
    void delete(User user);
    User findByUsername(String username);
}

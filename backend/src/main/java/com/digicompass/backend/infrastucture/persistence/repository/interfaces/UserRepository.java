package com.digicompass.backend.infrastucture.persistence.repository.interfaces;

import com.digicompass.backend.domain.model.User;
import com.digicompass.backend.infrastucture.persistence.document.UserDocument;
import com.digicompass.backend.infrastucture.persistence.mapper.UserMongoMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface UserRepository {

    User save(User user);
    Optional<User> findById(String id);
    List<User> findAll();
    void delete(User user);
}

package com.digicompass.backend.infrastucture.persistence.repository;

import com.digicompass.backend.domain.model.User;
import com.digicompass.backend.infrastucture.persistence.document.UserDocument;
import com.digicompass.backend.infrastucture.persistence.mapper.UserMongoMapper;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.UserRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

interface SpringDataUserRepository extends MongoRepository<UserDocument, String> {}

@Repository
public class MongoUserRepository implements UserRepository {

    private final SpringDataUserRepository repo;

    public MongoUserRepository(SpringDataUserRepository repo) {
        this.repo = repo;
    }

    @Override
    public User save(User user) {
        UserDocument saved = repo.save(UserMongoMapper.toDocument(user));
        return UserMongoMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(String id) {
        return repo.findById(id).map(UserMongoMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        if (repo.findAll() == null)
        {

        }
        return repo.findAll().stream()
                .map(UserMongoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(User user) {
        repo.delete(UserMongoMapper.toDocument(user));
    }
}

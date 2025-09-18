package com.digicompass.backend.application.services;

import com.digicompass.backend.domain.model.User;
import com.digicompass.backend.application.services.interfaces.UserService;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    public UserServiceImpl(@Qualifier("mongoUserRepository") UserRepository repo) {
        this.userRepository = repo;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUser(String id) {
        return userRepository.findById(id);
    }



    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }
}

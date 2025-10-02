package com.digicompass.backend.application.services;

import com.digicompass.backend.domain.models.User;
import com.digicompass.backend.application.interfaces.UserService;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.javapoet.ClassName;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Validated
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    private static final Logger LOGGER = Logger.getLogger( ClassName.class.getName() );
    @Autowired
    private UserService userService;

    public UserServiceImpl(@Qualifier("mongoUserRepository") UserRepository repo) {
        this.userRepository = repo;
    }

    @Override
    public List<User> getAllUsers() {
        LOGGER.log(Level.INFO, "Started fetching all of the users.");

        List<User> users = userRepository.findAll();
        LOGGER.log(Level.INFO, "Fetched all of the users.");
        return  users;
    }

    @Override
    public Optional<User> getUserById(String id) {
        LOGGER.log(Level.INFO, "Fetching user with id: {0}", id);

        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            LOGGER.log(Level.INFO, "User found with id: {0}", id);
        } else {
            LOGGER.log(Level.WARNING, "No user found with id: {0}", id);
        }

        return user;
    }

    @Override
    public void deleteUser(User user) {
        if (user == null) {
            LOGGER.log(Level.WARNING, "Attempted to delete a null user.");
            throw new IllegalArgumentException("User cannot be null");
        }

        LOGGER.log(Level.INFO, "Deleting user with id: {0}", user.getId());
        userRepository.delete(user);
        LOGGER.log(Level.INFO, "User deleted with id: {0}", user.getId());
    }
}

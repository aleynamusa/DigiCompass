package com.digicompass.backend.application.services;

import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.infrastucture.persistence.models.User;
import com.digicompass.backend.application.interfaces.UserService;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.UserInterface;
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

    private static final Logger LOGGER = Logger.getLogger( UserServiceImpl.class.getName() );


    UserInterface userRepository;

    private UserMapper userMapper;

    public UserServiceImpl(UserInterface userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    @Override
    public List<User> getAllUsers() {
        LOGGER.log(Level.INFO, "Started fetching all of the users.");

        List<User> users = userMapper.toDomain(userRepository.findAll());
        LOGGER.log(Level.INFO, "Fetched all of the users.");
        return  users;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        LOGGER.log(Level.INFO, "Fetching user with id: {0}", id);

        Optional<User> user = userRepository.findById(id)
                .map(userMapper::toDomain);

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
        userRepository.delete(userMapper.toEntity(user));
        LOGGER.log(Level.INFO, "User deleted with id: {0}", user.getId());
    }

}

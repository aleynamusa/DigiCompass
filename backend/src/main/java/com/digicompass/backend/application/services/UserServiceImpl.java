package com.digicompass.backend.application.services;

import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.application.models.User;
import com.digicompass.backend.application.interfaces.UserService;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userRepository;

    private final UserMapper userMapper;

    public UserServiceImpl(UserJpaRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    @Override
    public List<User> getAllUsers() {
        log.info("Started fetching all of the users.");

        List<User> users = userMapper.toDomain(userRepository.findAll());
        log.info("Fetched all of the users.");
        return  users;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        log.info("Fetching user with id: {}", id);

        Optional<User> user = userRepository.findById(id)
                .map(userMapper::toDomain);

        if (user.isPresent()) {
            log.info("User found with id: {}", id);
        } else {
            log.warn("No user found with id: {}", id);
        }

        return user;
    }

    @Override
    public void deleteUser(User user) {
        if (user == null) {
            log.warn("Attempted to delete a null user.");
            throw new IllegalArgumentException("User cannot be null");
        }

        log.info("Deleting user with id: {}", user.getId());
        userRepository.delete(userMapper.toEntity(user));
        log.info("User deleted with id: {}", user.getId());
    }

    @Override
    public boolean checkUsernameAvailability(String username) {
        return userRepository.findAllUsernames().contains(username);
    }

    @Override
    public boolean checkEmailAvailability(String email) {
        return userRepository.findAllEmails().contains(email);
    }

}

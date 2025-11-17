package com.digicompass.backend.application.services;

import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.application.models.User;
import com.digicompass.backend.application.interfaces.UserService;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        log.info("[SERVICE] Started fetching all of the users.");

        List<User> users = userMapper.toDomain(userRepository.findAll());
        log.info("[SERVICE] Fetched all of the users.");
        return  users;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        log.info("[SERVICE] Fetching user with id: {}", id);

        Optional<User> user = userRepository.findById(id)
                .map(userMapper::toDomain);

        if (user.isPresent()) {
            log.info("[SERVICE] User found with id: {}", id);
        } else {
            log.warn("[SERVICE] No user found with id: {}", id);
        }

        return user;
    }

    @Override
    public void deleteUser(User user) {
        if (user == null) {
            log.warn("[SERVICE] Attempted to delete a null user.");
            throw new IllegalArgumentException("User cannot be null");
        }

        log.info("[SERVICE] Deleting user with id: {}", user.getId());
        userRepository.delete(userMapper.toEntity(user));
        log.info("[SERVICE] User deleted with id: {}", user.getId());
    }

    @Override
    public boolean checkUsernameAvailability(String username) {
        try{
            if (username == null) {
                log.warn("[SERVICE] Attempted to check username is null.");
                throw new IllegalArgumentException("Username cannot be null");
            }
            log.info("[SERVICE] Checking username availability: {}", username);
            return userRepository.findAllUsernames().contains(username);
        }
        catch (Exception ex){
            log.warn("[SERVICE] Username not found: {}", username);
            return false;
        }

    }

    @Override
    public boolean checkEmailAvailability(String email) {
        try{
            if (email == null) {
                log.warn("[SERVICE] Attempted to check email is null.");
                throw new IllegalArgumentException("Email cannot be null");
            }
            log.info("[SERVICE] Checking email availability: {}", email);
            return userRepository.findAllEmails().contains(email);
        }
        catch (Exception e){
            log.warn("[SERVICE] Exception occurred while checking email availability: {}", e.getMessage());
            return false;
        }

    }

}

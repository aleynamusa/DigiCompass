package com.digicompass.backend.application.services;

import com.digicompass.backend.application.services.helpers.PasswordHasher;
import com.digicompass.backend.application.interfaces.AuthService;
import com.digicompass.backend.domain.models.User;
import com.digicompass.backend.infrastucture.persistence.repository.UserInterface;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.javapoet.ClassName;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Validated
public class AuthServiceImpl implements AuthService {

    UserInterface userRepository;

    private static final Logger LOGGER = Logger.getLogger( ClassName.class.getName() );

    public AuthServiceImpl(UserInterface userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User signUp(@Valid User user) {

        if (user == null) {
            LOGGER.log(Level.WARNING, "User cannot be null, when registering.");
            throw new IllegalArgumentException("User cannot be null");
        }

        LOGGER.log(Level.INFO, "Starting sign up process for username: {0}", user.getUsername());
        // hash the password
        String hashedPw = PasswordHasher.hash(user.getPassword());
        user.setPassword(hashedPw);
//        String token = UUID.randomUUID().toString();


        LOGGER.log(Level.FINE, "Password hashed successfully for username: {0}", user.getUsername());
        // save to DB
        User savedUser = userRepository.save(user);
        LOGGER.log(Level.INFO, "User signed up successfully with id: {0}", savedUser.getId());

        return savedUser;
    }

    @Override
    public User logIn(String username, String password) {

        LOGGER.log(Level.INFO, "Login attempt for username: {0}", username);

        User user = userRepository.findByUsername(username);
        if (user == null) {
            LOGGER.log(Level.WARNING, "Login failed: user not found for username: {0}", username);
            return null;
        }

        boolean success = PasswordHasher.verify(user.getPassword(), password);
        if (success) {
            LOGGER.log(Level.INFO, "Login successful for username: {0}", username);

            return user;
        } else {
            LOGGER.log(Level.WARNING, "Login failed: invalid password for username: {0}", username);
            return null;
        }
    }

}

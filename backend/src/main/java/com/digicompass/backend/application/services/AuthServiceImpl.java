package com.digicompass.backend.application.services;

import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.application.security.EmailValidator;
import com.digicompass.backend.application.security.PasswordValidator;
import com.digicompass.backend.application.services.helpers.PasswordHasher;
import com.digicompass.backend.application.interfaces.AuthService;
import com.digicompass.backend.domain.entity.UserEntity;
import com.digicompass.backend.infrastucture.persistence.models.User;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.UserInterface;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Validated
public class AuthServiceImpl implements AuthService {

    UserInterface userRepository;
    private final UserMapper userMapper;

    private static final Logger LOGGER = Logger.getLogger(AuthServiceImpl.class.getName());

    public AuthServiceImpl(UserInterface userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    @Override
    public User signUp(User user) {

        if (user == null) {
            LOGGER.log(Level.WARNING, "User cannot be null, when registering.");
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getAge() < 14){
            LOGGER.log(Level.WARNING, "User cannot be less than 14 years old.");
            throw new IllegalArgumentException("User cannot be less than 14 years old.");
        }

        String pwError = PasswordValidator.getValidationError(user.getPassword());
        if (pwError != null) {
            LOGGER.warning("Password validation failed: " + pwError);
            throw new IllegalArgumentException(pwError);
        }

        String emailError = EmailValidator.getValidationError(user.getEmail());
        if (emailError != null) {
            LOGGER.warning("Email validation failed: " + emailError);
            throw new IllegalArgumentException(emailError);
        }

        LOGGER.log(Level.INFO, "Starting sign up process for username: {0}", user.getUsername());
        // hash the password
        String hashedPw = PasswordHasher.hash(user.getPassword());
        user.setPassword(hashedPw);

        LOGGER.log(Level.FINE, "Password hashed successfully for username: {0}", user.getUsername());
        // save to DB
        UserEntity savedUser = userRepository.save(userMapper.toEntity(user));
        LOGGER.log(Level.INFO, "User signed up successfully with id: {0}", savedUser.getId());

        return userMapper.toDomain(savedUser);
    }

    @Override
    public User logIn(String username, String password) {

        LOGGER.log(Level.INFO, "Login attempt for username: {0}", username);

        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            LOGGER.log(Level.WARNING, "Login failed: user not found for username: {0}", username);
            return null;
        }

        boolean success = PasswordHasher.verify(user.getPassword(), password);
        if (success) {
            LOGGER.log(Level.INFO, "Login successful for username: {0}", username);

            return userMapper.toDomain(user);
        } else {
            LOGGER.log(Level.WARNING, "Login failed: invalid password for username: {0}", username);
            return null;
        }
    }

}

package com.digicompass.backend.application.services;

import com.digicompass.backend.application.services.helpers.PasswordHasher;
import com.digicompass.backend.application.interfaces.AuthService;
import com.digicompass.backend.domain.models.User;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    UserRepository userRepository;


    public AuthServiceImpl(@Qualifier("mongoUserRepository") UserRepository repo) {
        this.userRepository = repo;
    }

    @Override
    public User signUp(User user) {
        // hash the password
        String hashedPw = PasswordHasher.hash(user.getPassword());
        user.setPassword(hashedPw);
        // save to DB
        return userRepository.save(user);
    }

    @Override
    public boolean logIn(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) return false;
        String storedHash = user.getPassword();
        return PasswordHasher.verify(storedHash, password);
    }
}

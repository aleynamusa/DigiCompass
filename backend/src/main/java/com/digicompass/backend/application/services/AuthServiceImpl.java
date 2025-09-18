package com.digicompass.backend.application.services;

import com.digicompass.backend.application.services.helpers.PasswordHasher;
import com.digicompass.backend.application.services.interfaces.AuthService;
import com.digicompass.backend.domain.model.User;
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

//    @Override
//    public boolean logIn(String email, String password) {
////        User user = userRepository.findByEmail(email);
////        if (user == null) return false;
////        String storedHash = user.getPassword();
////        return hasher.verify(storedHash, password);
//    }
}

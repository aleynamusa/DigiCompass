package com.digicompass.backend.application.services.interfaces;

import com.digicompass.backend.domain.model.User;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    User signUp(User user);
    //boolean logIn(String email, String password);
}

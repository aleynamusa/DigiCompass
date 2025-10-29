package com.digicompass.backend.application.interfaces;

import com.digicompass.backend.application.models.User;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    User signUp( User user);
    User logIn(String email, String password);
}

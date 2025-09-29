package com.digicompass.backend.application.interfaces;

import com.digicompass.backend.domain.models.User;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    User signUp(User user);
    boolean logIn(String email, String password);
}

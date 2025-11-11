package com.digicompass.backend.application.interfaces;

import com.digicompass.backend.application.models.User;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface AuthService {
    User signUp(@Valid User user);
    Map<String, String> logIn(String email, String password);
    Map<String, String> refresh(Map<String, String> tokens);
}

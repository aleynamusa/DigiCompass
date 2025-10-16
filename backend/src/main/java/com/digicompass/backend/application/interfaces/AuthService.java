package com.digicompass.backend.application.interfaces;

import com.digicompass.backend.domain.models.User;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service

public interface AuthService {
    User signUp( User user);
    User logIn(String email, String password);
}

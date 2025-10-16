package com.digicompass.backend.presentation.controller;

import com.digicompass.backend.application.interfaces.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.javapoet.ClassName;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PasswordResetService passwordResetService;
    private static final Logger LOGGER = Logger.getLogger( ClassName.class.getName() );

    public AuthController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {

        LOGGER.log(Level.INFO, email);

        passwordResetService.createPasswordResetToken(email);
        return ResponseEntity.ok("Password reset link sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token,
                                                @RequestParam String password) {
        passwordResetService.resetPassword(token, password);
        return ResponseEntity.ok("Password successfully reset");
    }


}

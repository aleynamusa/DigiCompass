package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.PasswordResetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/password")
public class ResetPasswordController {

    private final PasswordResetService passwordResetService;

    @Autowired
    public ResetPasswordController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {

        log.info("Typed email: {}", email);

            passwordResetService.createPasswordResetToken(email);
            return ResponseEntity.ok("Password reset link sent");


    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam String token,
                                                @RequestParam String password) {
            passwordResetService.resetPassword(token, password);
            log.info("Password reset successfully");
            return ResponseEntity.ok("Password successfully reset");


    }
}

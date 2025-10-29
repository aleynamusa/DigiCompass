package com.digicompass.backend.presentation.controller;

import com.digicompass.backend.application.interfaces.AuthService;
import com.digicompass.backend.application.interfaces.UserService;
import com.digicompass.backend.application.models.User;
import com.digicompass.backend.presentation.controller.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;

    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody UserRequestDto request) {
        try {
            User user = new User(
                    null,
                    request.getUsername(),
                    request.getEmail(),
                    request.getBirthDate(),
                    request.getPassword()
            );
            User saved = authService.signUp(user);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id) {
        try{
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getBirthDate()));
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {

        try {
            return ResponseEntity.ok(userService.getAllUsers()
                    .stream()
                    .map(u -> new UserResponseDto(u.getId(), u.getUsername(), u.getEmail(), u.getBirthDate()))
                    .collect(Collectors.toList()));
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteUser(@RequestBody UserRequestDto request){
        try{
            User user = new User(request.getId(), request.getUsername(), request.getEmail(), request.getBirthDate(), request.getPassword());
            userService.deleteUser(user);
            return ResponseEntity.ok("User deleted successfully");
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }

    @PostMapping("/logIn")
    public ResponseEntity<?> logIn(@RequestBody LogInRequest request) {
        User user = authService.logIn(request.getUsername(), request.getPassword());
        if (user != null) {
            return ResponseEntity.ok(Map.of(
                    "username", user.getUsername(),
                    "email", user.getEmail()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password"));
        }
    }

    @GetMapping("/usernames")
    public ResponseEntity<Map<String, Object>> getUsernames(@RequestParam String username) {
        try{
            boolean available = userService.checkUsernameAvailability(username);
            Map<String, Object> response = new HashMap<>();
            response.put("available", available);
            response.put("message", available
            ? "Username is available"
            : "Username is taken");

            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/emails")
    public ResponseEntity<Map<String, Object>> getEmails( @RequestParam String email) {
        try{
            boolean available = userService.checkEmailAvailability(email);
            Map<String, Object> response = new HashMap<>();
            response.put("available", available);
            response.put("message", available
                    ? "Email is available"
                    : "Email is already registered");

            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
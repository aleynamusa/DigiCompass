package com.digicompass.backend.presentation.controller;

import com.digicompass.backend.application.interfaces.AuthService;
import com.digicompass.backend.application.interfaces.UserService;
import com.digicompass.backend.infrastucture.persistence.models.User;
import com.digicompass.backend.presentation.controller.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
            User user = new User(null, request.getUsername(), request.getEmail(), request.getAge(), request.getPassword());
            User saved = authService.signUp(user);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getAge());
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {

        return userService.getAllUsers()
                .stream()
                .map(u -> new UserResponseDto(u.getId(), u.getUsername(), u.getEmail(), u.getAge()))
                .collect(Collectors.toList());
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteUser(@RequestBody UserRequestDto request){
        User user = new User(request.getId(), request.getUsername(), request.getEmail(), request.getAge(), request.getPassword());
        userService.deleteUser(user);
        return ResponseEntity.ok("User deleted successfully");
    }

//    @PostMapping("/logIn")
//    public ResponseEntity<?> logIn(@RequestBody LogInRequest request) {
//        boolean logged = authService.logIn(request.getUsername(), request.getPassword(), request.getToken());
//        return ResponseEntity.ok(user);
//    }

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
}
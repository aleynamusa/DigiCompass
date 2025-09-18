package com.digicompass.backend.infrastucture.web.controller;

import com.digicompass.backend.application.services.interfaces.AuthService;
import com.digicompass.backend.application.services.interfaces.UserService;
import com.digicompass.backend.domain.model.User;
import com.digicompass.backend.infrastucture.web.dto.UserRequestDto;
import com.digicompass.backend.infrastucture.web.dto.UserResponseDto;
import com.digicompass.backend.application.services.helpers.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/users")
public class UserController {



    @Autowired
    private final UserService userService;

    @Autowired
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;

    }

    @PostMapping("/signUp")
    public UserResponseDto signUp(@RequestBody UserRequestDto request) {
        User user = new User(null, request.getUsername(), request.getEmail(), request.getAge(), request.getPassword());
        User saved = authService.signUp(user);
        return new UserResponseDto(saved.getId(), saved.getUsername(), saved.getEmail(), user.getAge());
    }

    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable String id) {
        User user = userService.getUser(id)
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
}
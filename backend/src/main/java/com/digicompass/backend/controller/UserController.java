package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.AuthService;
import com.digicompass.backend.application.interfaces.UserService;
import com.digicompass.backend.application.models.User;
import com.digicompass.backend.controller.dto.request.LogInRequest;
import com.digicompass.backend.controller.dto.request.UserRequestDto;
import com.digicompass.backend.controller.dto.response.UserResponseDto;
import com.digicompass.backend.controller.mapper.UserMapperController;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final AuthService authService;
    private final UserMapperController userMapperController;

    public UserController(UserService userService, AuthService authService, UserMapperController userMapperController) {
        this.userService = userService;
        this.authService = authService;
        this.userMapperController = userMapperController;
    }



//    @GetMapping("/{id}")
//    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id) {
//        try{
//            User user = userService.getUserById(id)
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//            return ResponseEntity.ok(new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getBirthDate()));
//        }
//        catch (Exception e){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
//        }
//
//    }
//
//    @GetMapping
//    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
//
//        try {
//            return ResponseEntity.ok(userService.getAllUsers()
//                    .stream()
//                    .map(u -> new UserResponseDto(u.getId(), u.getUsername(), u.getEmail(), u.getBirthDate()))
//                    .collect(Collectors.toList()));
//        }
//        catch (Exception e){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
//        }
//    }

//    @DeleteMapping()
//    public ResponseEntity<String> deleteUser(@RequestBody UserRequestDto request){
//        try{
//            User user = new User(request.getId(), request.getUsername(), request.getEmail(), request.getBirthDate(), request.getPassword(), request.getRole());
//            userService.deleteUser(user);
//            return ResponseEntity.ok("User deleted successfully");
//        }
//        catch (Exception e){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
//        }
//    }



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
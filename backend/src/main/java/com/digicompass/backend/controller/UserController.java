package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.AuthService;
import com.digicompass.backend.application.interfaces.UserService;
import com.digicompass.backend.application.models.User;
import com.digicompass.backend.controller.dto.request.LogInRequest;
import com.digicompass.backend.controller.dto.request.UserRequestDto;
import com.digicompass.backend.controller.dto.response.UserResponseDto;
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
                    request.getPassword(),
                    request.getRole()
            );
            User saved = authService.signUp(user);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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

    @PostMapping("/logIn")
    public ResponseEntity<?> logIn(@RequestBody LogInRequest request, HttpServletResponse response) {
        try {
            Map<String, String> tokens = authService.logIn(request.getUsername(), request.getPassword());
            boolean rememberMe = request.isRememberMe();
            if (tokens == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid username or password"));
            }
            if (rememberMe) {
                ResponseCookie cookie = ResponseCookie.from("refreshToken", tokens.get("refreshToken"))
                        .httpOnly(true)
                        .sameSite("Lax")
                        .secure(false)
                        .path("/")
                        .maxAge(7 * 24 * 60 * 60)
                        .build();
                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            }


            Map<String, String> body = Map.of("accessToken", tokens.get("accessToken"));
            return ResponseEntity.ok(body);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error while logging in"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = Arrays.stream(Optional.ofNullable(request.getCookies())
                        .orElse(new Cookie[0]))
                .filter(c -> "refreshToken".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No refresh token"));
        }

        var newTokens = authService.refresh(Map.of("refreshToken", refreshToken));
        if (newTokens == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid refresh token"));
        }

        //reissue cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", newTokens.get("refreshToken"))
                .httpOnly(true)
                .sameSite("Strict")
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(Map.of("accessToken", newTokens.get("accessToken")));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .sameSite("Strict")
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
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
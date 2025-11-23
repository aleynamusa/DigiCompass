package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.AuthService;
import com.digicompass.backend.application.interfaces.PasswordResetService;
import com.digicompass.backend.application.interfaces.UserService;
import com.digicompass.backend.controller.dto.request.LogInRequest;
import com.digicompass.backend.controller.dto.request.UserRequestDto;
import com.digicompass.backend.controller.mapper.UserMapperController;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserMapperController userMapperController;

    public AuthController(AuthService authService, UserMapperController userMapperController) {
        this.authService = authService;
        this.userMapperController = userMapperController;
    }


    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody UserRequestDto request) {
        try {
            UserRequestDto saved = userMapperController.toControllerRequest(authService.signUp(userMapperController.toModel(request)));
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

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
}

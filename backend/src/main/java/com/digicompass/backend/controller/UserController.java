package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.UserService;
import com.digicompass.backend.controller.dto.RouteDto;
import com.digicompass.backend.controller.dto.response.UserResponseDto;
import com.digicompass.backend.controller.mapper.RouteMapperController;
import com.digicompass.backend.controller.mapper.UserMapperController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final UserMapperController userMapperController;

    private final RouteMapperController routeMapperController;

    public UserController(UserService userService, UserMapperController userMapperController, RouteMapperController routeMapperController) {
        this.userService = userService;
        this.userMapperController = userMapperController;
        this.routeMapperController = routeMapperController;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id) {
        try{
            UserResponseDto user = userMapperController.toControllerResponse(userService.getUserById(id));

//            if (!user.isPublicProfile()) {
//                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This profile is private.");
//            }

            return ResponseEntity.ok(user);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
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


    @PostMapping(value = "/profilePictureUpdate/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUserProfilePicture(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        log.info("[CONTROLLER] Request to update profile picture for user ID {}", id);

        if (file == null || file.isEmpty()) {
            log.warn("[CONTROLLER] Profile picture update failed: empty file for user ID {}", id);
            return ResponseEntity.badRequest().body("File must not be empty");
        }

        try {
            userService.uploadProfilePicture(id, file);
            log.info("[CONTROLLER] Profile picture updated successfully for user ID {}", id);

            Map<String, Object> response = Map.of(
                    "message", "Profile picture updated successfully"
            );

            return ResponseEntity.ok(response);

        } catch (NoSuchElementException e) {
            log.error("[CONTROLLER] User ID {} not found: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        } catch (IllegalArgumentException e) {
            log.error("[CONTROLLER] Invalid image for user ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (IOException e) {
            log.error("[CONTROLLER] I/O error while updating profile picture for user ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file");

        } catch (Exception e) {
            log.error("[CONTROLLER] Unexpected error updating profile picture for user ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error occurred");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDto>> searchUser(@RequestParam String username) {
        return ResponseEntity.status(HttpStatus.OK).body(userMapperController.toController(userService.getByUsername(username)));
    }

    @PostMapping("/{id}/bio")
    public ResponseEntity<?> updateBio(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        userService.updateBio(id, request.get("bio"));
        return ResponseEntity.ok(Map.of("message", "Bio updated successfully"));
    }


    @PostMapping("/{id}/visibility")
    public ResponseEntity<?> updateProfileVisibility(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {

        userService.updateProfileVisibility(id, request.get("isPublicProfile"));
        return ResponseEntity.ok(Map.of("message", "Profile visibility updated"));
    }


    @GetMapping("/{id}/routes")
    public ResponseEntity<?> getRoutesByUserId(@PathVariable Long id) {
        try {
            List<RouteDto> routes = routeMapperController.toControllerRoute(
                    userService.getRoutesById(id)
            );
            return ResponseEntity.ok(routes);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}
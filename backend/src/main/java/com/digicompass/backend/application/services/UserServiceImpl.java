package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.RatingService;
import com.digicompass.backend.application.interfaces.S3Service;
import com.digicompass.backend.application.mapper.RouteMapper;
import com.digicompass.backend.application.mapper.UserMapper;
import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.application.models.User;
import com.digicompass.backend.application.interfaces.UserService;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userRepository;

    private final RouteJpaRepository routeRepository;

    private final RatingService ratingService;

    private final RouteMapper routeMapper;

    private final UserMapper userMapper;

    private final S3Service s3Service;

    private String message =  "User not found";

    public UserServiceImpl(UserJpaRepository userRepository, RouteJpaRepository routeRepository, RatingService ratingService, RouteMapper routeMapper, UserMapper userMapper, S3Service s3Service) {
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
        this.ratingService = ratingService;
        this.routeMapper = routeMapper;
        this.userMapper = userMapper;
        this.s3Service = s3Service;
    }

    @Override
    public List<User> getAllUsers() {
        log.info("[SERVICE] Started fetching all of the users.");

        List<User> users = userMapper.toDomain(userRepository.findAll());
        log.info("[SERVICE] Fetched all of the users.");
        return  users;
    }

    @Override
    public User getUserById(Long id) {
        log.info("[SERVICE] Fetching user with id: {}", id);

        var user = userMapper.toDomain(userRepository.findById(id).orElseThrow(() -> {
            log.warn("[SERVICE] No user found with id: {}", id);
            return new RuntimeException("User not found with id: " + id);
        }));

        if(user.getImageUrl() != null && !user.getImageUrl().isEmpty()){
            String imageUrl = s3Service.getPreSignedUrl(user.getImageUrl());
            user.setImageUrl(imageUrl);
        }

        return user;
    }


    @Override
    public void deleteUser(User user) {
        if (user == null) {
            log.warn("[SERVICE] Attempted to delete a null user.");
            throw new IllegalArgumentException("User cannot be null");
        }

        log.info("[SERVICE] Deleting user with id: {}", user.getId());
        userRepository.delete(userMapper.toEntity(user));
        log.info("[SERVICE] User deleted with id: {}", user.getId());
    }

    @Override
    public boolean checkUsernameAvailability(String username) {
        if (username == null) {
            log.warn("[SERVICE] Attempted to check username is null.");
            throw new IllegalArgumentException("Username cannot be null");
        }

        try{

            log.info("[SERVICE] Checking username availability: {}", username);
            return !userRepository.existsByUsername(username);
        }
        catch (Exception ex){
            log.warn("[SERVICE] Username not found: {}", username);
            throw ex;
        }
    }

    @Override
    public boolean checkEmailAvailability(String email) {

        if (email == null) {
            log.warn("[SERVICE] Attempted to check email is null.");
            throw new IllegalArgumentException("Email cannot be null");
        }

        try {
            log.info("[SERVICE] Checking email availability: {}", email);
            return !userRepository.existsByEmail(email);
        } catch (Exception e){
            log.warn("[SERVICE] Unexpected error: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public void uploadProfilePicture(Long id, MultipartFile image) throws IOException {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(message));

        String fileName = uploadImages(id, image);

        try {
            user.setImageUrl(fileName);
            userRepository.save(user);
        }
        catch (Exception e) {
            rollbackS3Uploads(fileName);
            throw e;
        }
    }

    @Override
    public List<User> getByUsername(String keyword) {
        try{
            log.info("[SERVICE] Searching users with keyword: {}", keyword);

            List<User> users = userMapper.toDomain(userRepository.findByUsernameContainingIgnoreCase(keyword));

            for(User user : users){
                if(user.getImageUrl() != null && !user.getImageUrl().isEmpty()){
                    String imageUrl = s3Service.getPreSignedUrl(user.getImageUrl());
                    user.setImageUrl(imageUrl);
                }
            }

            log.info("[SERVICE] Successfully fetched {} users.", users.size());
            return users;
        }
        catch (Exception e){
            log.error("[SERVICE] Error occurred while searching users by keyword: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to search users.", e);
        }
    }

    @Override
    public void updateProfileVisibility(Long userId, boolean isPublic) {
        try{
            log.info("[SERVICE] Updating profile visibility for userId: {} to {}", userId, isPublic);
            if(!userRepository.existsById(userId)){
                throw new IllegalArgumentException("User does not exist.");
            }
            User user = userMapper.toDomain(userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException(message)));

            user.setPublicProfile(isPublic);
            userRepository.save(userMapper.toEntity(user));
            log.info("[SERVICE] Successfully updated profile visibility for userId: {}", user.getId());
        }
        catch (Exception e){
            log.error("[SERVICE] Error occurred while updating profile visibility for userId: {}: {}", userId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update profile visibility.", e);
        }
    }

    @Override
    public void updateBio(Long userId, String bio) {
        log.info("[SERVICE] Updating bio for userId: {}", userId);

        if (bio == null || bio.isBlank()) {
            throw new IllegalArgumentException("Bio cannot be null or empty.");
        }

        try {
            User user = userRepository.findById(userId)
                    .map(userMapper::toDomain)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setBio(bio);
            userRepository.save(userMapper.toEntity(user));
            log.info("[SERVICE] Successfully updated bio for userId: {}", userId);

        } catch (RuntimeException e) {
            throw e; // don't convert expected exceptions
        } catch (Exception e) {
            log.error("Unexpected error updating bio", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update bio.", e);
        }
    }


    protected String uploadImages(Long userId, MultipartFile image) throws IOException {
        String key = "";

                if (image != null && !image.isEmpty()) {
                    key = s3Service.uploadImage("users/" + userId, image);
                }

        return key;
    }

    protected void rollbackS3Uploads(String key) {

            try {
                s3Service.deleteImage(key);
            } catch (Exception ex) {
                log.warn("Failed to delete S3 image during rollback: {}", key, ex);
            }

    }

    @Override
    public List<Route> getRoutesById(Long userId) {
        try{
            if (userRepository.findById(userId).isEmpty()) {
                throw new NoSuchElementException(message);
            }

            List<Route> routes = routeMapper.toDomain(routeRepository.findAllByUserId(userId));

            for (Route route : routes) {
                route.setAverageRating(ratingService.getRouteRating(route.getId()));
            }
            log.info("[SERVICE] Found {} routes for user with id: {}", routes.size(), userId);
            return routes;
        }
        catch (Exception ex){
            log.warn("[SERVICE] Failed to fetch routes by userId: {}", userId, ex);
            throw ex;
        }
    }

}

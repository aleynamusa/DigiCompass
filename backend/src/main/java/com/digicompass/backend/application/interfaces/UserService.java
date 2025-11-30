package com.digicompass.backend.application.interfaces;

import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.application.models.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    void deleteUser(User user);
    boolean checkUsernameAvailability(String username);
    boolean checkEmailAvailability( String email);
    void uploadProfilePicture(Long id, MultipartFile image) throws IOException;
    List<User> getByUsername(String keyword);
    void updateProfileVisibility(Long userId, boolean isPublic);
    void updateBio(Long userId, String bio);
    List<Route> getRoutesById(Long userId);
}

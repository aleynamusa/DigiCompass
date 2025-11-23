package com.digicompass.backend.application.interfaces;

import com.digicompass.backend.application.models.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    void deleteUser(User user);
    boolean checkUsernameAvailability(String username);
    boolean checkEmailAvailability( String email);
}

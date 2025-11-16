package com.digicompass.backend.unit.interfaces;

import com.digicompass.backend.unit.models.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    void deleteUser(User user);
    boolean checkUsernameAvailability(String username);
    boolean checkEmailAvailability( String email);
}

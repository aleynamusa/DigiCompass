package com.digicompass.backend.application.services.interfaces;

import com.digicompass.backend.domain.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUser(String id);
    void deleteUser(User user);
}

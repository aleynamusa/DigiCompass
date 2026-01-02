package com.digicompass.backend.repository.repositories;


import com.digicompass.backend.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findUserDocumentByUsername(String username);
    UserEntity findByEmail(String email);
    UserEntity findByUsername(String username);
    @Query("SELECT u.username FROM UserEntity u")
    List<String> findAllUsernames();
    @Query("SELECT u.email FROM UserEntity u")
    List<String>  findAllEmails();

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    List<UserEntity> findByUsernameContainingIgnoreCase(String username);
}

package com.digicompass.backend.repository;

import com.digicompass.backend.repository.entity.RoleEntity;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create"
})
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")

class UserJpaRepositoryTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserJpaRepository userRepository;

    private RoleEntity role;
    private UserEntity user1;
    private UserEntity user2;

    @BeforeEach
    void setup() {
        role = new RoleEntity();
        role.setRole("USER");
        entityManager.persist(role);

        user1 = new UserEntity();
        user1.setUsername("john_doe");
        user1.setEmail("john@example.com");
        user1.setBirthDate(LocalDate.of(1990, 5, 10));
        user1.setPassword("password123");
        user1.setRole(role);
        user1.setPublicProfile(true);
        entityManager.persist(user1);

        user2 = new UserEntity();
        user2.setUsername("jane_smith");
        user2.setEmail("jane@example.com");
        user2.setBirthDate(LocalDate.of(1985, 3, 15));
        user2.setPassword("password456");
        user2.setRole(role);
        user2.setPublicProfile(false);
        entityManager.persist(user2);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findUserDocumentByUsername_shouldReturnUser() {
        UserEntity found = userRepository.findUserDocumentByUsername("john_doe");
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void findByEmail_shouldReturnUser() {
        UserEntity found = userRepository.findByEmail("jane@example.com");
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("jane_smith");
    }

    @Test
    void findByUsername_shouldReturnUser() {
        UserEntity found = userRepository.findByUsername("john_doe");
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void findAllUsernames_shouldReturnAllUsernames() {
        List<String> usernames = userRepository.findAllUsernames();
        assertThat(usernames).containsExactlyInAnyOrder("john_doe", "jane_smith");
    }

    @Test
    void findAllEmails_shouldReturnAllEmails() {
        List<String> emails = userRepository.findAllEmails();
        assertThat(emails).containsExactlyInAnyOrder("john@example.com", "jane@example.com");
    }

    @Test
    void existsByEmail_shouldReturnTrueIfExists() {
        boolean exists = userRepository.existsByEmail("john@example.com");
        assertThat(exists).isTrue();

        boolean notExists = userRepository.existsByEmail("nonexistent@example.com");
        assertThat(notExists).isFalse();
    }

    @Test
    void existsByUsername_shouldReturnTrueIfExists() {
        boolean exists = userRepository.existsByUsername("jane_smith");
        assertThat(exists).isTrue();

        boolean notExists = userRepository.existsByUsername("unknown_user");
        assertThat(notExists).isFalse();
    }

    @Test
    void findByUsernameContainingIgnoreCase_shouldReturnUsersIgnoringCase() {
        List<UserEntity> users = userRepository.findByUsernameContainingIgnoreCase("JANE");
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getUsername()).isEqualTo("jane_smith");
    }
}

package com.digicompass.backend.integration;

import com.digicompass.backend.repository.entity.RoleEntity;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.RoleJpaRepository;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = com.digicompass.backend.BackendApplication.class
)
@AutoConfigureMockMvc
@Testcontainers
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class UserControllerIntegrationTest {
    private static final DockerImageName POSTGIS_IMAGE = DockerImageName
            .parse("postgis/postgis:17-3.5")
            .asCompatibleSubstituteFor("postgres");

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGIS_IMAGE)
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");


    @DynamicPropertySource
    static void registerPgProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");

        registry.add("spring.jpa.show-sql", () -> "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userRepo;

    @Autowired
    private RoleJpaRepository roleRepo;

    UserEntity user = new UserEntity();

    @BeforeEach
    void setup() {
        RoleEntity role = roleRepo.findByRole("USER");
        if (role == null) {
            role = new RoleEntity();
            role.setRole("USER");
            role = roleRepo.save(role);
        }

        user = userRepo.findByEmail("test@mail.com");
        if (user == null) {
            user = new UserEntity();
            user.setUsername("testuser");
            user.setEmail("test@mail.com");
            user.setBirthDate(LocalDate.of(1995, 1, 1));
            user.setPassword("oldpassword123");
            user.setRole(role);
            user = userRepo.save(user); // <-- FIX
        }
    }

    @AfterEach
    void tearDown() {
        if (user != null && user.getId() != null) {
            userRepo.deleteById(user.getId());
        }
    }

    @Test
    void shouldReturnUserById() throws Exception {
        mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@mail.com"))
                .andExpect(jsonPath("$.birthDate").value("1995-01-01"));
    }

    @Test
    void shouldReturnBadRequestWhenUserNotFound() throws Exception {
        mockMvc.perform(get("/users/9999"))
                .andExpect(status().isBadRequest());
    }



    @Test
    void usernameShouldBeTaken() throws Exception {
        mockMvc.perform(get("/users/usernames")
                        .param("username", "testuser")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(false)))
                .andExpect(jsonPath("$.message", is("Username is taken")));
    }

    @Test
    void usernameShouldBeAvailable() throws Exception {
        mockMvc.perform(get("/users/usernames")
                        .param("username", "newUser123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.message", is("Username is available")));
    }

    @Test
    void emailShouldBeTaken() throws Exception {
        mockMvc.perform(get("/users/emails")
                        .param("email", "test@mail.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(false)))
                .andExpect(jsonPath("$.message", is("Email is already registered")));
    }

    @Test
    void emailShouldBeAvailable() throws Exception {
        mockMvc.perform(get("/users/emails")
                        .param("email", "newUser123@gmail.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.message", is("Email is available")));
    }


}

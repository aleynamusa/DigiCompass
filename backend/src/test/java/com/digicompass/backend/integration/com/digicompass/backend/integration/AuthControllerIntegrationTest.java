package com.digicompass.backend.integration;


import com.digicompass.backend.controller.dto.request.LogInRequest;
import com.digicompass.backend.repository.entity.RoleEntity;
import com.digicompass.backend.repository.repositories.RoleJpaRepository;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

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
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private RoleJpaRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private javax.sql.DataSource dataSource;

    @BeforeEach
    void cleanupDatabase() throws Exception {
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {

            statement.execute("TRUNCATE TABLE rating CASCADE");
            statement.execute("TRUNCATE TABLE review CASCADE");
            statement.execute("TRUNCATE TABLE review_images CASCADE");
            statement.execute("TRUNCATE TABLE routes CASCADE");
            statement.execute("TRUNCATE TABLE users CASCADE");
        }


        if (!roleRepository.existsById(2L)) {
            RoleEntity role = new RoleEntity();
            role.setRole("user");
            roleRepository.save(role);
        }
    }

    @Test
    void signUp_createsUserSuccessfully() throws Exception {

        var request = new SignUpRequestTestDto(
                "john_doe",
                "john@example.com",
                LocalDate.of(2000, 1, 1),
                "Abcdef123!"
        );

        mockMvc.perform(post("/users/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }


    @Test
    void logIn_returnsTokensAndCookie() throws Exception {

        var request = new SignUpRequestTestDto(
                "maria",
                "maria@example.com",
                LocalDate.of(1998, 5, 20),
                "ValidPass123!"
        );

        mockMvc.perform(post("/users/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        var loginRequest = new LogInRequest(
                "maria",
                "ValidPass123!",
                true
        );

        mockMvc.perform(post("/users/logIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("refreshToken")))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("HttpOnly")))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Max-Age")));
    }


    @Test
    void logIn_invalidPassword_returnsUnauthorized() throws Exception {

        var request = new SignUpRequestTestDto(
                "ella",
                "ella@example.com",
                LocalDate.of(1999, 2, 2),
                "CorrectPass12!"
        );

        mockMvc.perform(post("/users/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        var loginRequest = new LogInRequest(
                "ella",
                "WRONG_PASS",
                false
        );

        mockMvc.perform(post("/users/logIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));
    }


    @Test
    void signUp_underAge_returnsBadRequest() throws Exception {
        var request = new SignUpRequestTestDto(
                "kid",
                "kid@example.com",
                LocalDate.now().minusYears(10), // age 10
                "ValidPass123!"
        );

        mockMvc.perform(post("/users/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("less than 14")));
    }

    @Test
    void signUp_invalidPassword_returnsBadRequest() throws Exception {
        var request = new SignUpRequestTestDto(
                "weakUser",
                "weak@example.com",
                LocalDate.of(2000, 1, 1),
                "abc"
        );

        mockMvc.perform(post("/users/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Password")));
    }

    @Test
    void signUp_invalidEmail_returnsBadRequest() throws Exception {
        var request = new SignUpRequestTestDto(
                "john",
                "notAnEmail",
                LocalDate.of(2000, 1, 1),
                "ValidPass123!"
        );

        mockMvc.perform(post("/users/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Email")));
    }


    @Test
    void signUp_duplicateEmail_returnsBadRequest() throws Exception {
        var request = new SignUpRequestTestDto(
                "john",
                "duplicate@example.com",
                LocalDate.of(2000, 1, 1),
                "ValidPass123!"
        );

        mockMvc.perform(post("/users/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void signUp_missingUsername_returnsBadRequest() throws Exception {
        var request = new SignUpRequestTestDto(
                null,
                "test@example.com",
                LocalDate.of(2000, 1, 1),
                "ValidPass123!"
        );
    }


    record SignUpRequestTestDto(
            String username,
            String email,
            LocalDate birthDate,
            String password
    ) {}


}

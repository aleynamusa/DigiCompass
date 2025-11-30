package com.digicompass.backend.integration;

import com.digicompass.backend.controller.dto.request.LogInRequest;
import com.digicompass.backend.repository.entity.RoleEntity;
import com.digicompass.backend.repository.repositories.RoleJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = com.digicompass.backend.BackendApplication.class
)
@AutoConfigureMockMvc
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private RoleJpaRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void cleanupDatabase(){

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

        mockMvc.perform(post("/auth/signUp")
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

        mockMvc.perform(post("/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        var loginRequest = new LogInRequest(
                "maria",
                "ValidPass123!",
                true
        );

        mockMvc.perform(post("/auth/logIn")
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

        mockMvc.perform(post("/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        var loginRequest = new LogInRequest(
                "ella",
                "WRONG_PASS",
                false
        );

        mockMvc.perform(post("/auth/logIn")
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

        mockMvc.perform(post("/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signUp_invalidPassword_returnsBadRequest() throws Exception {
        var request = new SignUpRequestTestDto(
                "weakUser",
                "weak@example.com",
                LocalDate.of(2000, 1, 1),
                "abc"
        );

        mockMvc.perform(post("/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signUp_invalidEmail_returnsBadRequest() throws Exception {
        var request = new SignUpRequestTestDto(
                "john",
                "notAnEmail",
                LocalDate.of(2000, 1, 1),
                "ValidPass123!"
        );

        mockMvc.perform(post("/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void signUp_duplicateEmail_returnsBadRequest() throws Exception {
        var request = new SignUpRequestTestDto(
                "john",
                "duplicate@example.com",
                LocalDate.of(2000, 1, 1),
                "ValidPass123!"
        );

        mockMvc.perform(post("/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/signUp")
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

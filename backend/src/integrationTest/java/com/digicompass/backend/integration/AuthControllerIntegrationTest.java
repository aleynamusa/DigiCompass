package com.digicompass.backend.integration;

import com.digicompass.backend.controller.dto.request.LogInRequest;
import com.digicompass.backend.repository.entity.RoleEntity;
import com.digicompass.backend.repository.repositories.RoleJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Arrays;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    void setup() {
        if (!roleRepository.existsById(2L)) {
            RoleEntity role = new RoleEntity();
            role.setRole("user");
            roleRepository.save(role);
        }
    }


    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private void signUpUser(String username, String email, LocalDate birthDate, String password) throws Exception {
        SignUpRequestTestDto request = new SignUpRequestTestDto(username, email, birthDate, password);

        mockMvc.perform(post("/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk());
    }

    private ResultActions loginUser(String username, String password, boolean rememberMe) throws Exception {
        LogInRequest login = new LogInRequest(username, password, rememberMe);

        return mockMvc.perform(post("/auth/logIn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(login)));
    }


    @Test
    void signUp_createsUserSuccessfully() throws Exception {
        SignUpRequestTestDto request = new SignUpRequestTestDto(
                "john_doe",
                "john@example.com",
                LocalDate.of(2000, 1, 1),
                "Abcdef123!"
        );

        mockMvc.perform(post("/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void logIn_returnsTokensAndCookie() throws Exception {
        signUpUser("maria", "maria@example.com", LocalDate.of(1998, 5, 20), "ValidPass123!");

        loginUser("maria", "ValidPass123!", true)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("refreshToken")))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("HttpOnly")))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Max-Age")));
    }



    @Test
    void refresh_returnsNewAccessToken_withRealAuthService() throws Exception {

        SignUpRequestTestDto request = new SignUpRequestTestDto(
                "bob",
                "bob@example.com",
                LocalDate.of(1990, 1, 1),
                "ValidPass123!"
        );

        mockMvc.perform(post("/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk());

        LogInRequest login = new LogInRequest("bob", "ValidPass123!", true);

        MvcResult loginResult = mockMvc.perform(post("/auth/logIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(login)))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("refreshToken")))
                .andReturn();

        String cookieHeader = loginResult.getResponse().getHeader("Set-Cookie");
        String refreshToken = extractCookieValue(cookieHeader, "refreshToken");

        mockMvc.perform(post("/auth/refresh")
                        .cookie(new Cookie("refreshToken", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("refreshToken")));
    }

    @Test
    void logout_returnsNewAccessToken_withRealAuthService() throws Exception {

        SignUpRequestTestDto request = new SignUpRequestTestDto(
                "test",
                "test@example.com",
                LocalDate.of(1990, 1, 1),
                "ValidPass123!"
        );

        mockMvc.perform(post("/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk());

        LogInRequest login = new LogInRequest("test", "ValidPass123!", true);

        MvcResult loginResult = mockMvc.perform(post("/auth/logIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(login)))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("refreshToken")))
                .andReturn();

        String cookieHeader = loginResult.getResponse().getHeader("Set-Cookie");
        String refreshToken = extractCookieValue(cookieHeader, "refreshToken");

        mockMvc.perform(post("/auth/refresh")
                        .cookie(new Cookie("refreshToken", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("refreshToken")));

        mockMvc.perform(post("/auth/logout")
                .cookie(new Cookie("refreshToken", "")))
                .andExpect(status().isNoContent())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("refreshToken")));
    }


    private String extractCookieValue(String cookieHeader, String name) {
        return Arrays.stream(cookieHeader.split(";"))
                .filter(c -> c.startsWith(name + "="))
                .map(c -> c.substring(name.length() + 1))
                .findFirst()
                .orElse(null);
    }


        record SignUpRequestTestDto(
            String username,
            String email,
            LocalDate birthDate,
            String password
    ) {}
}

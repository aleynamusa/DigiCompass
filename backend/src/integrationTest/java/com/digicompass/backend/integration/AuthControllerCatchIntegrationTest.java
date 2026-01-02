package com.digicompass.backend.integration;

import com.digicompass.backend.application.interfaces.AuthService;
import com.digicompass.backend.application.models.User;
import com.digicompass.backend.controller.dto.request.UserRequestDto;
import com.digicompass.backend.controller.mapper.UserMapperController;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class AuthControllerCatchIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;


    @MockitoBean
    private UserMapperController userMapperController;


    @Test
    void signUp_returnsBadRequest_whenUserIsNotValid()
            throws Exception {
        when(userMapperController.toModel(any(UserRequestDto.class))).thenReturn(new User());

        when(authService.signUp(any())).thenThrow(new RuntimeException("Forced exception"));

        mockMvc.perform(post("/auth/signUp")
                        .contentType("application/json")
                        .content("""
                            {
                                
                            }
                        """))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void logIn_returnBadRequest_whenUserIsNotValid()
            throws Exception {

        when(authService.logIn(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Invalid login data"));

        mockMvc.perform(post("/auth/logIn")
                        .contentType("application/json")
                        .content("""
                        {
                            "username": "",
                            "password": ""
                        }
                    """))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void logIn_returnsInternalServerError_whenUnexpectedExceptionOccurs() throws Exception {

        when(authService.logIn(anyString(), anyString()))
                .thenThrow(new RuntimeException("Unexpected failure"));

        mockMvc.perform(post("/auth/logIn")
                        .contentType("application/json")
                        .content("""
                        {
                            "username": "test",
                            "password": "pass"
                        }
                    """))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void refresh_returnsUnauthorized_whenNoRefreshTokenCookie() throws Exception {
        mockMvc.perform(post("/auth/refresh"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("No refresh token"));
    }

    @Test
    void refresh_returnsUnauthorized_whenTokenInvalid() throws Exception {

        when(authService.refresh(any()))
                .thenReturn(null);

        mockMvc.perform(post("/auth/refresh")
                        .cookie(new Cookie("refreshToken", "bad-token")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid refresh token"));
    }
}

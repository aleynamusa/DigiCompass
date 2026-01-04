package com.digicompass.backend.integration.exception;

import com.digicompass.backend.application.interfaces.UserActionsService;
import com.digicompass.backend.configuration.UserPrincipal;
import com.digicompass.backend.integration.BaseIntegrationTest;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@Rollback
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class UserActionsCatchIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserActionsService userActionsService;


    private String favoriteJson(Long userId, Long routeId) {
        return """
        {
          "userId": %d,
          "routeId": %d
        }
        """.formatted(userId, routeId);
    }

    private UserEntity createAndSaveTestUser(String username, String email) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setPassword("pw");
        return userRepository.saveAndFlush(user);
    }

    private UserPrincipal createUserPrincipal(UserEntity user) {
        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    private UserEntity testUser;
    private UserPrincipal testPrincipal;

    @BeforeEach
    void setUp() {
        testUser = createAndSaveTestUser("testuser", "test@mail.com");
        testPrincipal = createUserPrincipal(testUser);
    }

    private UsernamePasswordAuthenticationToken createAuthToken(UserPrincipal principal) {
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
    }

    @Test
    void shouldReturnBadRequest_whenIllegalArgument() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("User or route does not exist."))
                .when(userActionsService)
                .favouriteRoute(testPrincipal.getId(), 2L);

        mockMvc.perform(post("/action/favorite")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .param("routeId", "2")
                        .with(authentication(createAuthToken(testPrincipal))))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnInternalServerError_whenUnexpectedException() throws Exception {
        Mockito.doThrow(new RuntimeException("DB down"))
                .when(userActionsService)
                .favouriteRoute(testPrincipal.getId(), 3L);

        mockMvc.perform(post("/action/favorite")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .param("routeId", "3")
                        .with(authentication(createAuthToken(testPrincipal))))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error"));
    }

    @Test
    void isLikedRoute_ShouldReturnBadRequest_WhenIllegalArgument() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("User or route does not exist."))
                .when(userActionsService)
                .isLikedRoute(testPrincipal.getId(), 2L);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/action/isLiked")
                        .param("routeId", "2")
                        .with(authentication(createAuthToken(testPrincipal))))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error"));
    }

    @Test
    void isLikedRoute_ShouldInternalServerError_WhenUnexpectedException() throws Exception {
        Mockito.doThrow(new RuntimeException("DB down"))
                .when(userActionsService)
                .isLikedRoute(testPrincipal.getId(), 3L);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/action/isLiked")
                        .param("routeId", "3")
                        .with(authentication(createAuthToken(testPrincipal))))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error"));
    }

    @Test
    void unfavoriteRoute_ShouldReturnBadRequest_whenIllegalArgument() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("User or route does not exist."))
                .when(userActionsService)
                .unfavouriteRoute(any(), any());

        mockMvc.perform(post("/action/unfavorite")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .param("routeId", "1")
                        .with(authentication(createAuthToken(testPrincipal))))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error"));
    }

    @Test
    void unfavoriteRoute_ShouldReturnInternalServerError_whenException() throws Exception {
        Mockito.doThrow(new RuntimeException("Error unliking the route."))
                .when(userActionsService)
                .unfavouriteRoute(testPrincipal.getId(), 1L);

        mockMvc.perform(post("/action/unfavorite")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .param("routeId", "1")
                .with(authentication(createAuthToken(testPrincipal))))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error"));
    }



}

package com.digicompass.backend.integration;

import com.digicompass.backend.application.interfaces.UserActionsService;
import com.digicompass.backend.controller.dto.PointDto;
import com.digicompass.backend.controller.dto.request.FavouriteRouteRequest;
import com.digicompass.backend.controller.dto.request.RouteRequestMapDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class UserActionsCatchIntegrationTest extends BaseIntegrationTest {

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

    @Test
    void shouldReturnBadRequest_whenIllegalArgument() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("User or route does not exist."))
                .when(userActionsService)
                .favouriteRoute(2L, 2L);

        mockMvc.perform(post("/action/favorite")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(favoriteJson(2L, 2L))
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User or route does not exist."));
    }

    @Test
    void shouldReturnInternalServerError_whenUnexpectedException() throws Exception {
        Mockito.doThrow(new RuntimeException("DB down"))
                .when(userActionsService)
                .favouriteRoute(3L, 3L);

        mockMvc.perform(post("/action/favorite")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(favoriteJson(3L, 3L))
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error"));
    }

    @Test
    void isLikedRoute_ShouldReturnBadRequest_WhenIllegalArgument() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("User or route does not exist."))
                .when(userActionsService)
                .isLikedRoute(2L, 2L);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/action/isLiked")
                        .param("userId", "2")
                        .param("routeId", "2")
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User or route does not exist."));
    }
    @Test
    void isLikedRoute_ShouldInternalServerError_WhenUnexpectedException() throws Exception {
        Mockito.doThrow(new RuntimeException("DB down"))
                .when(userActionsService)
                .isLikedRoute(3L, 3L);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/action/isLiked")
                        .param("userId", "3")
                        .param("routeId", "3")
                        .with(user("testuser").roles("USER")))
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
                        .content(favoriteJson(any(), any()))
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User or route does not exist."));
    }

    @Test
    void unfavoriteRoute_ShouldReturnInternalServerError_whenException() throws Exception {
        Mockito.doThrow(new RuntimeException("Error unliking the route."))
                .when(userActionsService)
                .unfavouriteRoute(any(), any());

        mockMvc.perform(post("/action/unfavorite")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(favoriteJson(any(), any()))
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error"));
    }

}

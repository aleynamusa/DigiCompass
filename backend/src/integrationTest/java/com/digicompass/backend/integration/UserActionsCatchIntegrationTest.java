package com.digicompass.backend.integration;

import com.digicompass.backend.application.interfaces.UserActionsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
    void shouldFavoriteRoute_success() throws Exception {
        Mockito.doNothing().when(userActionsService).FavouriteRoute(1L, 1L);

        mockMvc.perform(post("/action/favorite")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(favoriteJson(1L, 1L)))
                .andExpect(status().isOk())
                .andExpect(content().string("Route favorited successfully."));
    }

    @Test
    void shouldUnfavoriteRoute_success() throws Exception {
        Mockito.doNothing().when(userActionsService).UnfavouriteRoute(1L, 1L);

        mockMvc.perform(post("/action/unfavorite")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(favoriteJson(1L, 1L)))
                .andExpect(status().isOk())
                .andExpect(content().string("Route unfavorited successfully."));
    }

    @Test
    void shouldReturnBadRequest_whenIllegalArgument() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("User or route does not exist."))
                .when(userActionsService)
                .FavouriteRoute(2L, 2L);

        mockMvc.perform(post("/action/favorite")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(favoriteJson(2L, 2L)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User or route does not exist."));
    }

    @Test
    void shouldReturnInternalServerError_whenUnexpectedException() throws Exception {
        Mockito.doThrow(new RuntimeException("DB down"))
                .when(userActionsService)
                .FavouriteRoute(3L, 3L);

        mockMvc.perform(post("/action/favorite")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(favoriteJson(3L, 3L)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error"));
    }
}

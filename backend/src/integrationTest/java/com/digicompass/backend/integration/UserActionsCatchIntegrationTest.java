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
    void shouldReturnBadRequest_whenIllegalArgument() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("User or route does not exist."))
                .when(userActionsService)
                .favouriteRoute(2L, 2L);

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
                .favouriteRoute(3L, 3L);

        mockMvc.perform(post("/action/favorite")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(favoriteJson(3L, 3L)))
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
                        .param("routeId", "2"))
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
                        .param("routeId", "3"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error"));
    }

}

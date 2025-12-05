package com.digicompass.backend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class UserActionsIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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

        mockMvc.perform(post("/action/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(favoriteJson(1L, 1L)))
                .andExpect(status().isOk())
                .andExpect(content().string("Route favorited successfully."));
    }

    @Test
    void shouldUnfavoriteRoute_success() throws Exception {

        mockMvc.perform(post("/action/unfavorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(favoriteJson(1L, 1L)))
                .andExpect(status().isOk())
                .andExpect(content().string("Route unfavorited successfully."));
    }

}

package com.digicompass.backend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                        .content(favoriteJson(1L, 1L))
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string("Route favorited successfully."));
    }

    @Test
    void shouldUnfavoriteRoute_success() throws Exception {

        mockMvc.perform(post("/action/unfavorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(favoriteJson(1L, 1L))
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string("Route unfavorited successfully."));
    }

    @Test
    void isLikedRouteShould_returnTrue() throws Exception {

        mockMvc.perform(post("/action/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(favoriteJson(1L, 2L))
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/action/isLiked")
                        .param("userId", "1")
                        .param("routeId", "2")
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void shouldIsLikedRoute_returnFalse() throws Exception {

        mockMvc.perform(get("/action/isLiked")
                        .param("userId", "1")
                        .param("routeId", "1")
                        .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }



}

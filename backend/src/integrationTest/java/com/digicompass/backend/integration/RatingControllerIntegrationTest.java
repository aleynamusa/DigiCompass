package com.digicompass.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class RatingControllerIntegrationTest extends BaseIntegrationTest {


    @Autowired
    private ObjectMapper objectMapper;

    private Long routeId = 1L;
    private Long userId = 1L;

    private String ratingJson(double rating) {
        return """
        {
          "rating": %s,
          "userId": {
              "id": %d,
              "username": "testuser"
          },
          "routeId": %d
        }
        """.formatted(rating, userId, routeId);
    }


    @Test
    void shouldCreateRating() throws Exception {
        mockMvc.perform(post("/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ratingJson(4.0))
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.rating").value(4.0))
                .andExpect(jsonPath("$.userId.id").value(1))
                .andExpect(jsonPath("$.userId.username").value("testuser"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.routeId").value(routeId));
    }

    @Test
    void shouldCreatingRatingCannotCreate() throws Exception{
        mockMvc.perform(post("/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                .content("")
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetRatingsByRoute() throws Exception {
        mockMvc.perform(get("/rating/route/" + routeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating").value(5.0));
    }

    @Test
    void shouldDeleteRating() throws Exception {

        String response = mockMvc.perform(post("/rating")
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ratingJson(3.5)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long ratingId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/rating/delete/" + ratingId)
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isNoContent());
    }


    @Test
    void shouldUpdateRating() throws Exception {
        // CREATE rating as testuser
        String createResponse = mockMvc.perform(post("/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ratingJson(3.0))
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long ratingId = objectMapper
                .readTree(createResponse)
                .get("id")
                .asLong();

        // UPDATE rating as same user
        mockMvc.perform(put("/rating/update/" + ratingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ratingJson(4.5))
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ratingId))
                .andExpect(jsonPath("$.rating").value(4.5))
                .andExpect(jsonPath("$.userId.username").value("admin"))
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.routeId").value(routeId));
    }

}
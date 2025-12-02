package com.digicompass.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@AutoConfigureMockMvc
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class RatingControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
                        .content(ratingJson(4.0)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.rating").value(4.0));
    }

    @Test
    void shouldCreatingRatingCannotCreate() throws Exception{
        mockMvc.perform(post("/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(""))
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ratingJson(3.5)))
                .andReturn().getResponse().getContentAsString();

        Long ratingId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/rating/delete/" + ratingId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldUpdateRating() throws Exception {
        String createResponse = mockMvc.perform(post("/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ratingJson(3.0)))
                .andReturn().getResponse().getContentAsString();

        Long ratingId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(put("/rating/update/" + ratingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ratingJson(4.5)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ratingId))
                .andExpect(jsonPath("$.rating").value(4.5));
    }
}
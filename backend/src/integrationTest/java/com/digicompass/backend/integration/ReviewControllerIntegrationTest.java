package com.digicompass.backend.integration;

import com.digicompass.backend.repository.repositories.ReviewJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ReviewControllerIntegrationTest extends BaseIntegrationTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ReviewJpaRepository reviewJpaRepository;

    private Long routeId = 1L;
    private Long userId = 1L;
    private String username = "testUser";

    private MockMultipartFile mockImage;

    @BeforeEach
    void setup() {
        reviewJpaRepository.deleteAll();
        mockImage = new MockMultipartFile(
                "images",
                "test-image.jpg",
                "image/jpeg",
                "FAKE_IMAGE_DATA".getBytes()
        );
    }



    @Test
    void shouldCreateReview() throws Exception {

        mockMvc.perform(multipart("/review")
                        .file(mockImage)
                        .param("review", "Amazing trail!")
                        .param("routeId", routeId.toString())
                        .param("userId.id", userId.toString())
                        .param("userId.username", username)
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.review").value("Amazing trail!"))
                .andExpect(jsonPath("$.images").exists())
                .andExpect(jsonPath("$.routeId").value(routeId))
                .andExpect(jsonPath("$.userId.id").value(userId))
                .andExpect(jsonPath("$.userId.username").value(username))
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }


    @Test
    void shouldUpdateReview() throws Exception {
        String createResponse = mockMvc.perform(multipart("/review")
                        .file(mockImage)
                        .param("review", "Nice route at first!")
                        .param("routeId", routeId.toString())
                        .param("userId.id", userId.toString())
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long reviewId = objectMapper.readTree(createResponse).get("id").asLong();

        MockMultipartFile newImage = new MockMultipartFile(
                "images",
                "new-image.jpg",
                "image/jpeg",
                "NEW_FAKE_IMAGE_DATA".getBytes()
        );

        String existingUrlsJson = "[\"https://old.image.com/img1.jpg\"]";

        mockMvc.perform(multipart("/review/update/" + reviewId)
                        .file(newImage)
                        .param("review", "Updated review text")
                        .param("routeId", routeId.toString())
                        .param("userId.id", userId.toString())
                        .param("existingImageUrls", existingUrlsJson)
                        .with(user("testuser").roles("USER"))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> { request.setMethod("PUT"); return request; })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.review").value("Updated review text"))
                .andExpect(jsonPath("$.id").value(reviewId))
                .andExpect(jsonPath("$.images").exists())
                .andExpect(jsonPath("$.routeId").value(routeId))
                .andExpect(jsonPath("$.userId.id").value(userId))
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn();
    }


    @Test
    void shouldGetReviewsByRoute() throws Exception {

        mockMvc.perform(multipart("/review")
                        .file(mockImage)
                        .param("review", "Great!")
                        .param("routeId", routeId.toString())
                        .param("userId.id", userId.toString())
                        .param("userId.username", username)
                        .with(user("testuser").roles("USER"))
                )
                         .andExpect(status().isCreated());

        mockMvc.perform(get("/review/route/" + routeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].routeId").value(routeId))
                .andExpect(jsonPath("$[0].userId.id").value(userId))
        .andExpect(jsonPath("$[0].review").value("Great!"));
    }

    @Test
    void shouldDeleteReview() throws Exception {
        String createResponse = mockMvc.perform(multipart("/review")
                        .file(mockImage)
                        .param("review", "Delete me!")
                        .param("routeId", routeId.toString())
                        .param("userId.id", userId.toString())
                        .param("userId.username", username)
                        .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long reviewId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(delete("/review/delete/" + reviewId)
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isNoContent());
    }
}

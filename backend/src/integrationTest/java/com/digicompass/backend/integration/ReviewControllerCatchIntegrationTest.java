package com.digicompass.backend.integration;

import com.digicompass.backend.BackendApplication;
import com.digicompass.backend.application.interfaces.ReviewService;
import com.digicompass.backend.controller.mapper.ReviewMapperController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = BackendApplication.class
)
@AutoConfigureMockMvc
@Testcontainers
public class ReviewControllerCatchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private ReviewMapperController reviewMapper; // if needed



    private MockMultipartFile mockImage;


    @Test
    void createReview_shouldReturnBadRequest_whenServiceThrowsIllegalArgument() throws Exception {
        Mockito.when(reviewService.createReview(any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid input"));

        mockMvc.perform(multipart("/review")
                        .param("review", "bad input"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReview_shouldReturnInternalServerError_whenUnexpectedExceptionOccurs() throws Exception {
        Mockito.when(reviewService.createReview(any(), any()))
                .thenThrow(new RuntimeException("DB failed"));

        mockMvc.perform(multipart("/review"))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void getReviewsByRoute_shouldReturnBadRequest_whenServiceThrowsIllegalArgument() throws Exception {
        Mockito.when(reviewService.getReviewsByRoute(1L))
                .thenThrow(new IllegalArgumentException("Invalid route"));

        mockMvc.perform(get("/review/route/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getReviewsByRoute_shouldReturnInternalServerError_whenServiceThrowsException() throws Exception {
        Mockito.when(reviewService.getReviewsByRoute(1L))
                .thenThrow(new RuntimeException("Unexpected failure"));

        mockMvc.perform(get("/review/route/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateReview_shouldReturnBadRequest_whenServiceThrowsIllegalArgument() throws Exception {
        Mockito.when(reviewService.updateReview(any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid update"));


        mockMvc.perform(
                        multipart("/review/update/1")
                                .with(request -> { request.setMethod("PUT"); return request; })
                )
                .andExpect(status().isBadRequest());

    }


    @Test
    void updateReview_shouldReturnInternalServerError_whenServiceThrowsException() throws Exception {
        Mockito.when(reviewService.updateReview(any(), any(), any()))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(
                        multipart("/review/update/1")
                                .with(request -> { request.setMethod("PUT"); return request; })
                )
                .andExpect(status().isInternalServerError());

    }


    @Test
    void deleteReview_shouldReturnNotFound_whenServiceThrowsIllegalArgument() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Not found"))
                .when(reviewService).deleteReview(1L);

        mockMvc.perform(delete("/review/delete/1"))
                .andExpect(status().isNotFound());
    }
    @Test
    void deleteReview_shouldReturnForbidden_whenAccessDenied() throws Exception {
        Mockito.doThrow(new AccessDeniedException("Forbidden"))
                .when(reviewService).deleteReview(1L);

        mockMvc.perform(delete("/review/delete/1"))
                .andExpect(status().isForbidden());
    }
    @Test
    void deleteReview_shouldReturnInternalServerError_whenUnexpectedErrorOccurs() throws Exception {
        Mockito.doThrow(new RuntimeException("Unexpected"))
                .when(reviewService).deleteReview(1L);

        mockMvc.perform(delete("/review/delete/1"))
                .andExpect(status().isInternalServerError());
    }


}

package com.digicompass.backend.integration;

import com.digicompass.backend.application.interfaces.ReviewService;
import com.digicompass.backend.controller.mapper.ReviewMapperController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@WithMockUser(username = "testuser", roles = "USER")
public class ReviewControllerCatchIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private ReviewMapperController reviewMapper; // if needed


    @Test
    void createReview_shouldReturnInternalServerError_whenServiceThrowsIllegalArgument() throws Exception {
        Mockito.when(reviewService.createReview(any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid input"));

        mockMvc.perform(multipart("/review")
                        .param("review", "bad input"))
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
    void updateReview_shouldReturnInternalServerError_whenServiceThrowsIllegalArgument() throws Exception {
        Mockito.when(reviewService.updateReview(any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid update"));


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
                .andExpect(status().isBadRequest());
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

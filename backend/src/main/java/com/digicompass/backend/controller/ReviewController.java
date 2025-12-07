package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.ReviewService;
import com.digicompass.backend.application.models.Review;
import com.digicompass.backend.controller.dto.request.ReviewRequestDto;
import com.digicompass.backend.controller.dto.response.ReviewResponseDto;
import com.digicompass.backend.controller.mapper.ReviewMapperController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/review")
public class  ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapperController reviewMapper;

    public ReviewController(ReviewService reviewService, ReviewMapperController reviewMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponseDto> createReview(@ModelAttribute ReviewRequestDto reviewRequestDto) {
        try {
            ReviewResponseDto response = reviewMapper.toDtoResponse(reviewService.createReview(
                    reviewMapper.toModel(reviewRequestDto),
                    reviewRequestDto.getImages()));

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (IllegalArgumentException ex) {
            log.warn("[CONTROLLER] Validation error creating review: {}", ex.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);

        } catch (Exception ex) {
            log.error("[CONTROLLER] Error creating review", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByRoute(@PathVariable Long routeId) {
        try {
            List<ReviewResponseDto> response = reviewMapper.toDtosResponse(reviewService.getReviewsByRoute(routeId));
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException _) {
            log.warn("[CONTROLLER] Invalid route ID: {}", routeId);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);

        } catch (Exception ex) {
            log.error("[CONTROLLER] Error fetching reviews for route {}", routeId, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable Long id,
            @ModelAttribute ReviewRequestDto reviewRequestDto
    ) {
        try {

            List<String> existingImageUrls = reviewRequestDto.getExistingImageUrlsList();

            ReviewResponseDto response = reviewMapper.toDtoResponse(reviewService.updateReview(
                    reviewMapper.toModel(reviewRequestDto),
                    reviewRequestDto.getImages(),
                    existingImageUrls));
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            log.warn("[CONTROLLER] Validation error updating review {}: {}", id, ex.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);

        } catch (Exception ex) {
            log.error("[CONTROLLER] Error updating review {}", id, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);

            return ResponseEntity
                    .noContent()
                    .build();

        } catch (IllegalArgumentException _) {
            log.warn("[CONTROLLER] Review not found: {}", id);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(null);

        } catch (AccessDeniedException _) {
            log.warn("[CONTROLLER] Access denied deleting review {}", id);
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(null);

        } catch (Exception ex) {
            log.error("[CONTROLLER] Error deleting review {}", id, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
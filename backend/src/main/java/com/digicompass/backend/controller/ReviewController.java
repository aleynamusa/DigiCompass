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
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapperController reviewMapper;

    public ReviewController(ReviewService reviewService, ReviewMapperController reviewMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createReview(@ModelAttribute ReviewRequestDto reviewRequestDto) {
        try {
            Review createdReview = reviewService.createReview(
                    reviewMapper.toModel(reviewRequestDto),
                    reviewRequestDto.getImages()
            );

            ReviewResponseDto response = reviewMapper.toDtoResponse(createdReview);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (IllegalArgumentException ex) {
            log.warn("[CONTROLLER] Validation error creating review: {}", ex.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());

        } catch (Exception ex) {
            log.error("[CONTROLLER] Error creating review", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create review");
        }
    }

    @GetMapping("/route/{routeId}")
    public ResponseEntity<?> getReviewsByRoute(@PathVariable Long routeId) {
        try {
            List<Review> reviews = reviewService.getReviewsByRoute(routeId);

            if (reviews.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .build();
            }

            List<ReviewResponseDto> response = reviewMapper.toDtosResponse(reviews);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            log.warn("[CONTROLLER] Invalid route ID: {}", routeId);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());

        } catch (Exception ex) {
            log.error("[CONTROLLER] Error fetching reviews for route {}", routeId, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch reviews");
        }
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateReview(
            @PathVariable Long id,
            @ModelAttribute ReviewRequestDto reviewRequestDto
    ) {
        try {
            Review reviewModel = reviewMapper.toModel(reviewRequestDto);
            reviewModel.setId(id);

            List<String> existingImageUrls = reviewRequestDto.getExistingImageUrlsList();

            Review updatedReview = reviewService.updateReview(
                    reviewModel,
                    reviewRequestDto.getImages(),
                    existingImageUrls
            );

            ReviewResponseDto response = reviewMapper.toDtoResponse(updatedReview);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            log.warn("[CONTROLLER] Validation error updating review {}: {}", id, ex.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());

        } catch (Exception ex) {
            log.error("[CONTROLLER] Error updating review {}", id, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update review");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);

            return ResponseEntity
                    .noContent()
                    .build();

        } catch (IllegalArgumentException ex) {
            log.warn("[CONTROLLER] Review not found: {}", id);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Review not found with ID: " + id);

        } catch (AccessDeniedException ex) {
            log.warn("[CONTROLLER] Access denied deleting review {}", id);
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You are not allowed to delete this review");

        } catch (Exception ex) {
            log.error("[CONTROLLER] Error deleting review {}", id, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete review");
        }
    }
}
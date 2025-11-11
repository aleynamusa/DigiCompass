package com.digicompass.backend.presentation.controller;


import com.digicompass.backend.application.interfaces.ReviewService;
import com.digicompass.backend.presentation.controller.dto.ReviewDto;
import com.digicompass.backend.presentation.controller.dto.response.ReviewResponseDto;
import com.digicompass.backend.presentation.controller.mapper.ReviewMapperController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapperController reviewMapper;
    private final ReviewMapperController reviewMapperController;

    public ReviewController(ReviewService reviewService, ReviewMapperController reviewMapper, ReviewMapperController reviewMapperController) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
        this.reviewMapperController = reviewMapperController;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createReview(
            @ModelAttribute ReviewDto reviewDto
    ) {
        try {
            boolean created = reviewService.createReview(reviewMapper.toModel(reviewDto), reviewDto.getImages());

            if (created) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Review created successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body("Invalid data or unable to create review");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating review: " + ex.getMessage());
        }
    }

    @GetMapping("/route/{id}")
    public ResponseEntity<?> getReviewsByRoute(@PathVariable Long id) {
        try{
            List<ReviewResponseDto> reviews = reviewMapper.toDtosResponse(reviewService.getReviewsByRoute(id));
            if (reviews.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No reviews found");
            }

            return ResponseEntity.ok().body(reviews);
        }
        catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        try {
            boolean deleted = reviewService.deleteReview(id);

            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Review not found with ID: " + id);
            }

            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not allowed to delete this review.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + ex.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @ModelAttribute ReviewDto reviewDto) {
        try{
            boolean response = reviewService.updateReview(reviewMapperController.toModel(reviewDto), reviewDto.getImages());
            return response ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        }
        catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

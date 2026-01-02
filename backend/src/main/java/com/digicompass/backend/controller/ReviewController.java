package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.ReviewService;
import com.digicompass.backend.controller.dto.request.ReviewRequestDto;
import com.digicompass.backend.controller.dto.response.ReviewResponseDto;
import com.digicompass.backend.controller.mapper.ReviewMapperController;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/review")
public class  ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapperController reviewMapper;

    @Autowired
    public ReviewController(ReviewService reviewService, ReviewMapperController reviewMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponseDto> createReview(@Valid @ModelAttribute ReviewRequestDto reviewRequestDto) throws IOException {
            ReviewResponseDto response = reviewMapper.toDtoResponse(reviewService.createReview(
                    reviewMapper.toModel(reviewRequestDto),
                    reviewRequestDto.getImages()));

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);


    }

    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByRoute(@PathVariable Long routeId) {

            List<ReviewResponseDto> response = reviewMapper.toDtosResponse(reviewService.getReviewsByRoute(routeId));
            return ResponseEntity.ok(response);


    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable Long id,
            @Valid @ModelAttribute ReviewRequestDto reviewRequestDto
    ) throws IOException {

            List<String> existingImageUrls = reviewRequestDto.getExistingImageUrlsList();

            ReviewResponseDto response = reviewMapper.toDtoResponse(reviewService.updateReview(
                    reviewMapper.toModel(reviewRequestDto),
                    reviewRequestDto.getImages(),
                    existingImageUrls));
            return ResponseEntity.ok(response);


    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
            reviewService.deleteReview(id);

            return ResponseEntity
                    .noContent()
                    .build();


    }
}
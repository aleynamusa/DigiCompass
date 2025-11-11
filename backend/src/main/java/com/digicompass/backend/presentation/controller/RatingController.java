package com.digicompass.backend.presentation.controller;

import com.digicompass.backend.application.interfaces.RatingService;
import com.digicompass.backend.application.interfaces.ReviewService;
import com.digicompass.backend.presentation.controller.dto.RatingDto;
import com.digicompass.backend.presentation.controller.dto.ReviewDto;
import com.digicompass.backend.presentation.controller.mapper.RatingMapperController;
import com.digicompass.backend.presentation.controller.mapper.ReviewMapperController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rating")
public class RatingController {
    private final RatingService ratingService;
    private final RatingMapperController ratingMapper;

    public RatingController(RatingService ratingService, RatingMapperController ratingMapper) {
        this.ratingService = ratingService;
        this.ratingMapper = ratingMapper;
    }


    @PostMapping
    public ResponseEntity<?> createRating(@RequestBody RatingDto rating) {
        try {
            boolean response = ratingService.addRating(ratingMapper.toModel(rating));

            return response ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        }
        catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/route/{id}")
    public ResponseEntity<?> getRatingsByRoute(@PathVariable Long id) {
        try{
            List<RatingDto> reviews = ratingMapper.toDto(ratingService.getRatingsByRouteId(id));

            return ResponseEntity.ok().body(reviews);
        }
        catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}

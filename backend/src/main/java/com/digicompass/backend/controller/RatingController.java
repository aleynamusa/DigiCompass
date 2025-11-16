package com.digicompass.backend.controller;

import com.digicompass.backend.unit.interfaces.RatingService;
import com.digicompass.backend.controller.dto.RatingDto;
import com.digicompass.backend.controller.mapper.RatingMapperController;
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

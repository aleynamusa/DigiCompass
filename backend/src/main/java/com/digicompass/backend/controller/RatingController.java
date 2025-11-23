package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.RatingService;
import com.digicompass.backend.controller.dto.RatingDto;
import com.digicompass.backend.controller.mapper.RatingMapperController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
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
            log.info("Creating rating {}", rating);
            RatingDto response = ratingMapper.toDto(ratingService.addRating(ratingMapper.toModel(rating)));

            log.debug("Created rating {}", response);
            if(response.getId() != null) {
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(response);
            }else{
                log.warn("Failed to create rating");
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Failed to create rating");

            }

        }
        catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create rating");
        }
    }

    @GetMapping("/route/{id}")
    public ResponseEntity<?> getRatingsByRoute(@PathVariable Long id) {
        log.info("Getting ratings by routeId {}", id);
        try{
            List<RatingDto> ratings = ratingMapper.toDto(ratingService.getRatingsByRouteId(id));

            log.debug("Fetched {} ratings of route with id: {}", ratings.size(), id);
            log.debug("Fetched {}", ratings);

            if (ratings.isEmpty()) {
                log.info("No ratings found for route with id: {}", id);
                return ResponseEntity.ok(ratings);
            }
            else{
                log.info("Found {} ratings for route with id: {}", ratings.size(), id);
                return ResponseEntity.ok(ratings);
            }
        }
        catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch ratings for route with id: " + id);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRating(@PathVariable Long id, @RequestBody RatingDto rating) {
        log.info("Updating rating {}", rating);
        try {
            rating.setId(id);
            RatingDto updatedRating = ratingMapper.toDto(ratingService.updateRating(ratingMapper.toModel(rating)));
            log.debug("Updated rating {}", updatedRating);

            if(updatedRating.getId() != null) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(updatedRating);
            }
            else{
                log.warn("Failed to fetch rating with id: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to fetch rating with id: " + id);
            }

        }catch (Exception ex){
            log.error(ex.getMessage(), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRating(@PathVariable Long id) {
        log.info("Deleting rating {}", id);
        try{
            ratingService.deleteRating(id);

            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
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

package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.RatingService;
import com.digicompass.backend.controller.dto.RatingDto;
import com.digicompass.backend.controller.mapper.RatingMapperController;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public RatingController(RatingService ratingService, RatingMapperController ratingMapper) {
        this.ratingService = ratingService;
        this.ratingMapper = ratingMapper;
    }

@RolesAllowed("")
    @PostMapping
    public ResponseEntity<RatingDto> createRating(@RequestBody RatingDto rating) {

            log.info("[CONTROLLER] Creating rating {}", rating);
            RatingDto response = ratingMapper.toDto(ratingService.addRating(ratingMapper.toModel(rating)));

            log.debug("[CONTROLLER] Created rating {}", response);
            if(response.getId() != null) {
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(response);
            }else{
                log.warn("[CONTROLLER] Failed to create rating");
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(null);

            }


    }

    @GetMapping("/route/{id}")
    public ResponseEntity<List<RatingDto>> getRatingsByRoute(@PathVariable Long id) {
        log.info("[CONTROLLER] Getting ratings by routeId {}", id);

            List<RatingDto> ratings = ratingMapper.toDto(ratingService.getRatingsByRouteId(id));

            log.debug("[CONTROLLER] Fetched {} ratings of route with id: {}", ratings.size(), id);
            log.debug("[CONTROLLER] Fetched {}", ratings);

            if (ratings.isEmpty()) {
                log.info("[CONTROLLER] No ratings found for route with id: {}", id);
                return ResponseEntity.ok(ratings);
            }
            else{
                log.info("[CONTROLLER] Found {} ratings for route with id: {}", ratings.size(), id);
                return ResponseEntity.ok(ratings);
            }

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<RatingDto> updateRating(@PathVariable Long id, @RequestBody RatingDto rating) {
        log.info("Updating rating {}", rating);

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
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }


    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        log.info("Deleting rating {}", id);
        try{
            ratingService.deleteRating(id);

            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();

        } catch (AccessDeniedException _) {
            log.warn("[CONTROLLER] Access denied deleting review {}", id);
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(null);
        }
    }
}

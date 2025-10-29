package com.digicompass.backend.presentation.controller.dto;

import com.digicompass.backend.application.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RatingDto {
    private Long id;

    private Double rating; // 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0

    private User userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private RouteDto routeId;
}

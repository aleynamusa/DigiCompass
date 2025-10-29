package com.digicompass.backend.application.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Rating {

    private Long id;

    private Double rating; // 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0

    private User userId;

    private LocalDateTime createdAt =  LocalDateTime.now();

    private LocalDateTime updatedAt =  LocalDateTime.now();

    private Route routeId;
}



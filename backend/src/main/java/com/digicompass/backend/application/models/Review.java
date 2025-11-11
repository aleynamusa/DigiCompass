package com.digicompass.backend.application.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Review {
    private Long id;

    private String review;

    private User userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<String> images = new ArrayList<>();

    private Long routeId;

}

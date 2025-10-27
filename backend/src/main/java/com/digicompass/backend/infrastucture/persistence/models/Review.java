package com.digicompass.backend.infrastucture.persistence.models;

import com.digicompass.backend.domain.entity.RouteEntity;
import com.digicompass.backend.domain.entity.RouteImageEntity;
import com.digicompass.backend.domain.entity.UserEntity;
import jakarta.persistence.*;
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

    private List<RouteImage> images = new ArrayList<>();

    private Route routeId;

}

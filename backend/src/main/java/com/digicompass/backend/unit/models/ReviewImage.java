package com.digicompass.backend.unit.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImage {
    private Long id;

    private String imageUrl;

    private Long reviewId;
}

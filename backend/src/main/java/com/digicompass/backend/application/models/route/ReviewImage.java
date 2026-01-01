package com.digicompass.backend.application.models.route;

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

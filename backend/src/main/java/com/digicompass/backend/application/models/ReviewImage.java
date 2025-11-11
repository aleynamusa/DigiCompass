package com.digicompass.backend.application.models;

import com.digicompass.backend.domain.entity.ReviewEntity;
import com.digicompass.backend.domain.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class ReviewImage {
    private Long id;

    private String imageUrl;

    private Long reviewId;
}

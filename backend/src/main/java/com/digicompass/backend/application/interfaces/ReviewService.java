package com.digicompass.backend.application.interfaces;

import com.digicompass.backend.application.models.Review;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReviewService {
    List<Review> getReviewsByRoute(Long routeId);
}

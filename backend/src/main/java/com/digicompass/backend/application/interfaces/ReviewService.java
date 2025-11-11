package com.digicompass.backend.application.interfaces;

import com.digicompass.backend.application.models.Review;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface ReviewService {
    List<Review> getReviewsByRoute(Long routeId);
    boolean createReview(Review review, List<MultipartFile> images) throws IOException;
    boolean updateReview(Review review, List<MultipartFile> images) throws IOException;
    boolean deleteReview(Long reviewId);
}

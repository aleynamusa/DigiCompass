package com.digicompass.backend.unit.interfaces;

import com.digicompass.backend.unit.models.Review;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface ReviewService {
    List<Review> getReviewsByRoute(Long routeId);
    Review createReview(Review review, List<MultipartFile> images) throws IOException;
    Review updateReview(Review review, List<MultipartFile> images, List<String> existingImageUrls) throws IOException;
    void deleteReview(Long reviewId);
}

package com.digicompass.backend.unit.services;

import com.digicompass.backend.unit.interfaces.ReviewService;
import com.digicompass.backend.unit.interfaces.S3Service;
import com.digicompass.backend.unit.mapper.ReviewMapper;
import com.digicompass.backend.unit.models.Review;
import com.digicompass.backend.repository.entity.ReviewEntity;
import com.digicompass.backend.repository.entity.ReviewImageEntity;
import com.digicompass.backend.repository.repositories.ReviewJpaRepository;
import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewJpaRepository reviewRepo;
    private final ReviewMapper reviewMapper;
    private final RouteJpaRepository routeRepo;

    private final S3Service s3Service;


    public ReviewServiceImpl(ReviewJpaRepository reviewRepo, ReviewMapper reviewMapper, RouteJpaRepository routeRepo,
                              S3Service s3Service) {
        this.reviewRepo = reviewRepo;
        this.reviewMapper = reviewMapper;
        this.routeRepo = routeRepo;
        this.s3Service = s3Service;
    }

    @Override
    public List<Review> getReviewsByRoute(Long routeId) {
        try {
            if (routeId == null || routeId <= 0 || !routeRepo.getAllIds().contains(routeId)) {
                log.warn("[Service] Invalid routeId provided: {0}", routeId);
                throw new IllegalArgumentException("Route ID must not be null or negative.");
            }

            var entities = reviewRepo.getReviewsByRoute(routeId);
            if (entities == null) {
                log.warn("[Service] No reviews found for routeId: {0}", routeId);
                return List.of();
            }

            List<Review> reviews = reviewMapper.toDomain(entities);

            for (Review review : reviews) {
                if (review.getImages() != null && !review.getImages().isEmpty()) {
                    List<String> preSignedUrls = review.getImages().stream()
                            .map(s3Service::getPreSignedUrl)
                            .toList();
                    review.setImages(preSignedUrls);
                }
            }

            log.info("[Service] Fetched {0} reviews for routeId {1}", new Object[]{reviews.size(), routeId});
            return reviews;

        } catch (IllegalArgumentException e) {
            log.warn("[Service] Validation error fetching reviews: {0}", e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error while fetching reviews for routeId {0}: {1}",
                    new Object[]{routeId, e.getMessage()});
            throw new RuntimeException("Unexpected error while fetching reviews.", e);
        }
    }

    @Override
    @Transactional
    public Review createReview(Review review, List<MultipartFile> images) throws IOException {
        validateRouteId(review.getRouteId());

        List<String> imageKeys = uploadImages(review.getRouteId(), images);

        try {
            ReviewEntity entity = reviewMapper.toEntity(review);

            List<ReviewImageEntity> imageEntities = imageKeys.stream()
                    .map(key -> {
                        ReviewImageEntity img = new ReviewImageEntity();
                        img.setImageUrl(key);
                        img.setReview(entity);
                        return img;
                    })
                    .toList();

            entity.setImages(imageEntities);

            ReviewEntity saved = reviewRepo.save(entity);
            return reviewMapper.toDomain(saved);

        } catch (Exception e) {
            rollbackS3Uploads(imageKeys);
            throw new RuntimeException("Failed to create review", e);
        }
    }

    private void validateRouteId(Long routeId) {
        if (routeId == null || routeId <= 0) {
            throw new IllegalArgumentException("Invalid route ID: " + routeId);
        }
        if (!routeRepo.existsById(routeId)) {
            throw new IllegalArgumentException("Route not found: " + routeId);
        }
    }

    private List<String> uploadImages(Long routeId, List<MultipartFile> images) throws IOException {
        List<String> keys = new ArrayList<>();
        if (images != null) {
            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) {
                    String key = s3Service.uploadImage("reviews/" + routeId, image);
                    keys.add(key);
                }
            }
        }
        return keys;
    }

    private void rollbackS3Uploads(List<String> keys) {
        keys.forEach(key -> {
            try {
                s3Service.deleteImage(key);
            } catch (Exception ex) {
                log.warn("Failed to delete S3 image during rollback: {}", key, ex);
            }
        });
    }

    @Override
    public Review updateReview(Review review, List<MultipartFile> images, List<String> existingImageUrls) throws IOException {
        log.debug("[SERVICE] Attempting to update review with id={}", review.getId());
        log.debug("[SERVICE] Existing image URLs to keep: {}", existingImageUrls);

        if (review.getId() == null) {
            log.warn("[SERVICE] Review ID is required for update");
            throw new IllegalArgumentException("Review ID cannot be null");
        }

        try {
            var existingReviews = reviewRepo.getReviewsByRoute(review.getRouteId());
            ReviewEntity existing = existingReviews.stream()
                    .filter(r -> r.getId().equals(review.getId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Review not found with id=" + review.getId()));

            // Update review text
            existing.setReview(review.getReview());

            // Log current images before filtering
            log.debug("[SERVICE] Current images in DB: {}",
                    existing.getImages().stream()
                            .map(ReviewImageEntity::getImageUrl)
                            .collect(Collectors.toList()));

            // Handle existing images
            if (existingImageUrls != null && !existingImageUrls.isEmpty()) {
                // Extract S3 keys from full URLs (remove domain and query parameters)
                List<String> existingKeys = existingImageUrls.stream()
                        .map(this::extractS3Key)
                        .collect(Collectors.toList());

                log.debug("[SERVICE] Extracted keys from URLs: {}", existingKeys);

                // Remove images that the user deleted (keep only those in existingKeys list)
                existing.getImages().removeIf(img -> {
                    boolean shouldRemove = !existingKeys.contains(img.getImageUrl());
                    log.debug("[SERVICE] Image {}: shouldRemove={}", img.getImageUrl(), shouldRemove);
                    return shouldRemove;
                });
                log.debug("[SERVICE] After filtering, kept {} existing images", existing.getImages().size());
            } else {
                // If no existing images specified, remove all
                log.debug("[SERVICE] No existing images to keep, clearing all");
                existing.getImages().clear();
            }

            // Add new uploaded images
            if (images != null && !images.isEmpty()) {
                log.debug("[SERVICE] Adding {} new images", images.size());
                for (MultipartFile image : images) {
                    if (image != null && !image.isEmpty()) {
                        String key = s3Service.uploadImage("reviews/" + review.getRouteId(), image);
                        ReviewImageEntity imageEntity = new ReviewImageEntity();
                        imageEntity.setImageUrl(key);
                        imageEntity.setReview(existing);
                        existing.getImages().add(imageEntity);
                        log.debug("[SERVICE] Added new image: {}", key);
                    }
                }
            }

            ReviewEntity updated = reviewRepo.save(existing);
            log.info("[SERVICE] Successfully updated review with id={}, total images: {}",
                    review.getId(), updated.getImages().size());

            return reviewMapper.toDomain(updated);

        } catch (IllegalArgumentException e) {
            log.warn("[SERVICE] Validation error while updating review id={}: {}", review.getId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("[SERVICE] Unexpected error while updating review id={}: {}", review.getId(), e.getMessage(), e);
            throw new RuntimeException("Unexpected error while updating review", e);
        }
    }

    // Helper method to extract S3 key from full URL
    private String extractS3Key(String url) {
        try {
            // If it's already just a key (no http), return as-is
            if (!url.startsWith("http")) {
                return url;
            }

            // Extract key from URL like:
            // https://digicompassip.s3.amazonaws.com/reviews/1/image.png?params...
            // Result should be: reviews/1/image.png

            String[] parts = url.split("amazonaws.com/");
            if (parts.length > 1) {
                // Remove query parameters if present
                String keyWithParams = parts[1];
                int queryStart = keyWithParams.indexOf('?');
                if (queryStart > 0) {
                    return keyWithParams.substring(0, queryStart);
                }
                return keyWithParams;
            }

            // If pattern doesn't match, return original
            return url;
        } catch (Exception e) {
            log.warn("[SERVICE] Failed to extract S3 key from URL: {}", url);
            return url;
        }
    }

    @Override
    public void deleteReview(Long reviewId) {
        log.debug("[SERVICE] Attempting to delete review with id={}", reviewId);

        try {
            var reviews = reviewRepo.getReviewsByRoute(null);
            reviews.stream()
                    .filter(r -> r.getId().equals(reviewId))
                    .findFirst()
                    .ifPresent(review -> {
                        if (review.getImages() != null) {
                            for (var img : review.getImages()) {
                                try {
                                    s3Service.deleteImage(img.getImageUrl());
                                    log.info("[SERVICE] Deleted image from S3: {}", img.getImageUrl());
                                } catch (Exception e) {
                                    log.warn("[SERVICE] Failed to delete image {} from S3: {}", img.getImageUrl(), e.getMessage());
                                }
                            }
                        }
                    });
            reviewRepo.deleteById(reviewId);
            log.info("[SERVICE] Successfully deleted review with id={}", reviewId);


        } catch (Exception e) {
            log.error("[SERVICE] Error deleting review with id={}: {}", reviewId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete review", e);
        }
    }



}
package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.ReviewService;
import com.digicompass.backend.application.interfaces.S3Service;
import com.digicompass.backend.application.mapper.ReviewMapper;
import com.digicompass.backend.application.models.Review;
import com.digicompass.backend.repository.entity.ReviewEntity;
import com.digicompass.backend.repository.entity.ReviewImageEntity;
import com.digicompass.backend.repository.repositories.ReviewJpaRepository;
import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

            var reviews = reviewMapper.toDomainList(reviewRepo.getReviewsByRoute(routeId));
            if (reviews == null) {
                log.warn("[Service] No reviews found for routeId: {0}", routeId);
                return List.of();
            }


            for (Review review : reviews) {
                if (review.getImages() != null && !review.getImages().isEmpty()) {
                    List<String> preSignedUrls = review.getImages().stream()
                            .map(s3Service::getPreSignedUrl)
                            .toList();
                    review.setImages(preSignedUrls);
                }
            }

            log.info("[Service] Fetched {} reviews for routeId {}", reviews.size(), routeId);
            return reviews;

        } catch (IllegalArgumentException e) {
            log.warn("[Service] Validation error fetching reviews: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error while fetching reviews for routeId {}: {}",
                    routeId, e.getMessage());
            throw new RuntimeException("Unexpected error while fetching reviews.", e);
        }
    }

//    @Override
//    public Review createReview(Review review, List<MultipartFile> images) throws IOException {
//        validateRouteId(review.getRouteId());
//
//        List<String> imageKeys = uploadImages(review.getRouteId(), images);
//
//        try {
//            ReviewEntity entity = reviewMapper.toEntity(review);
//
//            for (String key : imageKeys) {
//                ReviewImageEntity img = new ReviewImageEntity();
//                img.setImageUrl(key);
//                img.setReview(entity);
//                entity.getImages().add(img);
//            }
//
//            ReviewEntity saved = reviewRepo.save(entity);
//            return reviewMapper.toDomain(saved);
//
//        } catch (Exception e) {
//            rollbackS3Uploads(imageKeys);
//            throw new RuntimeException("Failed to create review", e);
//        }
//    }

    @Override
    public Review createReview(Review review, List<MultipartFile> images) throws IOException {
        validateRouteId(review.getRouteId());

        List<String> imageKeys = s3Service.uploadImages(review.getRouteId(), images);
        review.setImages(imageKeys);

        try {
            return reviewMapper.toDomain(reviewRepo.save(reviewMapper.toEntity(review)));
        } catch (Exception e) {
            s3Service.rollbackS3Uploads(imageKeys);
            throw e;
        }
    }

    protected void validateRouteId(Long routeId) {
        if (routeId == null || routeId <= 0) {
            throw new IllegalArgumentException("Invalid route ID: " + routeId);
        }
        if (!routeRepo.existsById(routeId)) {
            throw new IllegalArgumentException("Route not found: " + routeId);
        }
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

            existing.setReview(review.getReview());

            log.debug("[SERVICE] Current images in DB: {}",
                    existing.getImages().stream()
                            .map(ReviewImageEntity::getImageUrl)
                            .collect(Collectors.toList()));

            if (existingImageUrls != null && !existingImageUrls.isEmpty()) {
                List<String> existingKeys = existingImageUrls.stream()
                        .map(this::extractS3Key)
                        .collect(Collectors.toList());

                log.debug("[SERVICE] Extracted keys from URLs: {}", existingKeys);

                existing.getImages().removeIf(img -> {
                    boolean shouldRemove = !existingKeys.contains(img.getImageUrl());
                    log.debug("[SERVICE] Image {}: shouldRemove={}", img.getImageUrl(), shouldRemove);
                    return shouldRemove;
                });
                log.debug("[SERVICE] After filtering, kept {} existing images", existing.getImages().size());
            } else {
                log.debug("[SERVICE] No existing images to keep, clearing all");
                existing.getImages().clear();
            }

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

    protected String extractS3Key(String url) {
        try {
            if (!url.startsWith("http")) {
                return url;
            }

            String[] parts = url.split("amazonaws.com/");
            if (parts.length > 1) {
                String keyWithParams = parts[1];
                int queryStart = keyWithParams.indexOf('?');
                if (queryStart > 0) {
                    return keyWithParams.substring(0, queryStart);
                }
                return keyWithParams;
            }

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
            if(reviewRepo.existsById(reviewId)){
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
            }
            else{
                throw new RuntimeException("Review with id=" + reviewId + " does not exist");
            }


        } catch (Exception e) {
            log.error("[SERVICE] Error deleting review with id={}: {}", reviewId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete review", e);
        }
    }

}
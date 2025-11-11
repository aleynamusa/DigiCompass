package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.ReviewService;
import com.digicompass.backend.application.interfaces.S3Service;
import com.digicompass.backend.application.mapper.ReviewMapper;
import com.digicompass.backend.application.models.Review;
import com.digicompass.backend.domain.entity.ReviewEntity;
import com.digicompass.backend.domain.entity.ReviewImageEntity;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.ReviewInterface;
import com.digicompass.backend.infrastucture.persistence.repository.interfaces.RouteInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewInterface reviewRepo;
    private final ReviewMapper reviewMapper;
    private final RouteInterface routeRepo;

    private final S3Service s3Service;


    public ReviewServiceImpl(ReviewInterface reviewRepo, ReviewMapper reviewMapper, RouteInterface routeRepo,
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
                log.warn("Invalid routeId provided: {0}", routeId);
                throw new IllegalArgumentException("Route ID must not be null or negative.");
            }

            var entities = reviewRepo.getReviewsByRoute(routeId);
            if (entities == null) {
                log.warn("No reviews found for routeId: {0}", routeId);
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

            log.info("Fetched {0} reviews for routeId {1}", new Object[]{reviews.size(), routeId});
            return reviews;

        } catch (IllegalArgumentException e) {
            log.warn("Validation error fetching reviews: {0}", e.getMessage());
            throw e;

        } catch (Exception e) {
//            LOGGER.log(Level.SEVERE, "Unexpected error while fetching reviews for routeId {0}: {1}",
//                    new Object[]{routeId, e.getMessage()});
            throw new RuntimeException("Unexpected error while fetching reviews.", e);
        }
    }

    @Override
    public boolean createReview(Review review, List<MultipartFile> images) throws IOException {
        if (review.getRouteId() == null || !routeRepo.getAllIds().contains(review.getRouteId())) {
            log.warn("Invalid routeId: {0}", review.getRouteId());
            throw new IllegalArgumentException("Invalid route ID");
        }

        List<String> uploadedKeys = new ArrayList<>();
        try {
            List<ReviewImageEntity> imageEntities = new ArrayList<>();
            if (images != null && !images.isEmpty()) {
                for (MultipartFile image : images) {
                    if (image != null && !image.isEmpty()) {
                        String key = s3Service.uploadImage("reviews/" + review.getRouteId(), image);
                        uploadedKeys.add(key);

                        ReviewImageEntity imageEntity = new ReviewImageEntity();
                        imageEntity.setImageUrl(key);
                        imageEntities.add(imageEntity);
                    }
                }
            }

            ReviewEntity reviewEntity = reviewMapper.toEntity(review);
            for (ReviewImageEntity img : imageEntities) {
                img.setReview(reviewEntity);
            }
            reviewEntity.setImages(imageEntities);

            boolean saved = reviewRepo.createReview(reviewEntity);
            if (!saved) throw new RuntimeException("Failed to save review");

            log.info( "Created review for routeId: {0} with {1} images",
                    new Object[]{review.getRouteId(), imageEntities.size()});
            return true;

        } catch (Exception e) {
//            LOGGER.log(Level.SEVERE, "Rolling back due to error: {0}", e.getMessage());
            for (String key : uploadedKeys) {
                try {
                    s3Service.deleteImage(key);
                } catch (Exception ex) {
                    log.warn("Failed to delete S3 image {0} during rollback", key);
                }
            }
            throw e;
        }
    }

    @Override
    public boolean updateReview(Review review, List<MultipartFile> images) throws IOException {
        log.debug("[SERVICE] Attempting to update review with id={}", review.getId());

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

            List<ReviewImageEntity> imageEntities = new ArrayList<>();
            if (images != null && !images.isEmpty()) {
                for (MultipartFile image : images) {
                    if (image != null && !image.isEmpty()) {
                        String key = s3Service.uploadImage("reviews/" + review.getRouteId(), image);
                        ReviewImageEntity imageEntity = new ReviewImageEntity();
                        imageEntity.setImageUrl(key);
                        imageEntity.setReview(existing);
                        imageEntities.add(imageEntity);
                    }
                }
                existing.setImages(imageEntities);
            }

            boolean updated = reviewRepo.updateReview(existing);
            if (updated) {
                log.info("[SERVICE] Successfully updated review with id={}", review.getId());
            } else {
                log.warn("[SERVICE] Update may not have persisted for review id={}", review.getId());
            }

            return updated;

        } catch (IllegalArgumentException e) {
            log.warn("[SERVICE] Validation error while updating review id={}: {}", review.getId(), e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("[SERVICE] Unexpected error while updating review id={}: {}", review.getId(), e.getMessage(), e);
            throw new RuntimeException("Unexpected error while updating review", e);
        }
    }


    @Override
    public boolean deleteReview(Long reviewId) {
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

            boolean response =  reviewRepo.deleteReviewByRoute(reviewId);
            log.info("[SERVICE] Successfully deleted review with id={}", reviewId);
            return response;

        } catch (Exception e) {
            log.error("[SERVICE] Error deleting review with id={}: {}", reviewId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete review", e);
        }
    }



}
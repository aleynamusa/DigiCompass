package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.S3Service;
import com.digicompass.backend.repository.entity.ReviewImageEntity;
import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import com.digicompass.backend.application.mapper.ReviewMapper;
import com.digicompass.backend.repository.entity.ReviewEntity;
import com.digicompass.backend.application.models.Review;
import com.digicompass.backend.repository.repositories.ReviewJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    ReviewJpaRepository reviewRepoMock;

    @Mock
    S3Service s3ServiceMock;

    @Mock
    ReviewMapper reviewMapperMock;

    @Mock
    RouteJpaRepository routeRepoMock;

    @Mock
    private MultipartFile file1;

    @Mock
    private MultipartFile file2;

    @Mock
    private MultipartFile emptyFile;

    @InjectMocks
    ReviewServiceImpl reviewServiceMock;

    private Long validRouteId;

    @BeforeEach
    void setUp() {
        validRouteId = 1L;
    }


    //GET REVIEWS
    @Test
    void getReviewsByRoute_ThrowsException_WhenRouteIsNullOrZero()
    {
        assertThrows(IllegalArgumentException.class, () -> reviewServiceMock.getReviewsByRoute(null));
        assertThrows(IllegalArgumentException.class, () -> reviewServiceMock.getReviewsByRoute(0L));
        assertThrows(IllegalArgumentException.class, () -> reviewServiceMock.getReviewsByRoute(-9L));

    }

    @Test
    void getReviewsByRoute_ThrowsIllegalArgumentException_WhenRouteIdNotFound() {

        Long routeId = 9L;
        when(routeRepoMock.getAllIds()).thenReturn(List.of(1L, 2L, 3L));

        assertThrows(IllegalArgumentException.class, () -> reviewServiceMock.getReviewsByRoute(routeId));
    }

    @Test
    void getReviewsByRoute_ReturnsMappedReviews_WhenValidId() {
        List<Long> validIds = List.of(1L, 2L, 3L);
        when(routeRepoMock.getAllIds()).thenReturn(validIds);

        List<ReviewEntity> entityList = List.of(new ReviewEntity(), new ReviewEntity());
        when(reviewRepoMock.getReviewsByRoute(validRouteId)).thenReturn(entityList);

        var domainList = List.of(new Review(), new Review());
        when(reviewMapperMock.toDomainList(entityList)).thenReturn(domainList);

        List<Review> result = reviewServiceMock.getReviewsByRoute(validRouteId);

        assertEquals(2, result.size());
        verify(reviewRepoMock).getReviewsByRoute(validRouteId);
        verify(reviewMapperMock).toDomainList(entityList);
    }

    @Test
    void getReviewsByRoute_MapsImageUrlsToPresigned_WhenImagesPresent() {

        when(routeRepoMock.getAllIds()).thenReturn(List.of(1L));

        List<ReviewEntity> entityList = List.of(new ReviewEntity());
        when(reviewRepoMock.getReviewsByRoute(validRouteId)).thenReturn(entityList);

        Review reviewWithImages = new Review();
        reviewWithImages.setImages(List.of("img1.png", "img2.png"));
        when(reviewMapperMock.toDomainList(entityList)).thenReturn(List.of(reviewWithImages));

        when(s3ServiceMock.getPreSignedUrl("img1.png")).thenReturn("pre-img1");
        when(s3ServiceMock.getPreSignedUrl("img2.png")).thenReturn("pre-img2");

        List<Review> result = reviewServiceMock.getReviewsByRoute(validRouteId);

        assertEquals(1, result.size());
        assertEquals(List.of("pre-img1", "pre-img2"), result.get(0).getImages());

        verify(s3ServiceMock).getPreSignedUrl("img1.png");
        verify(s3ServiceMock).getPreSignedUrl("img2.png");
    }

    @Test
    void getReviewsByRoute_ReturnsEmptyList_WhenRepoReturnsNull() {
        when(routeRepoMock.getAllIds()).thenReturn(List.of(1L));
        when(reviewRepoMock.getReviewsByRoute(validRouteId)).thenReturn(null);

        List<Review> result = reviewServiceMock.getReviewsByRoute(validRouteId);

        assertTrue(result.isEmpty());
        verify(reviewMapperMock, never()).toDomainList(List.of());
    }

    @Test
    void getReviewsByRoute_ThrowsRuntimeException_WhenRepoThrowsError() {
        when(routeRepoMock.getAllIds()).thenReturn(List.of(1L));
        when(reviewRepoMock.getReviewsByRoute(validRouteId)).thenThrow(new RuntimeException("DB failure"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reviewServiceMock.getReviewsByRoute(validRouteId)
        );

        assertTrue(exception.getMessage().contains("Unexpected error"));
        verify(reviewRepoMock).getReviewsByRoute(validRouteId);
    }


    //CREATE REVIEWS
    @Test
    void createReview_throwsNullPointer_whenMapperReturnsNull() throws Exception {
        Review review = new Review();
        review.setRouteId(1L);

        List<MultipartFile> images = List.of();

        when(routeRepoMock.existsById(1L)).thenReturn(true);

        when(reviewMapperMock.toEntity(review)).thenReturn(null);

        assertThrows(NullPointerException.class,
                () -> reviewServiceMock.createReview(review, images));

        verify(reviewMapperMock).toEntity(review);
    }



//    @Test
//    void createReview_ThrowsRuntimeExceptionAndRollsBack_WhenUnexpectedErrorOccurs() throws Exception {
//        Review review = new Review();
//        review.setRouteId(1L);
//
//        List<MultipartFile> images = List.of();
//        List<String> uploadedKeys = List.of("img1", "img2");
//
//        when(routeRepoMock.existsById(1L)).thenReturn(true);
//
//        doReturn(uploadedKeys).when(reviewServiceMock).uploadImages(1L, images);
//
//        ReviewEntity entity = new ReviewEntity();
//        doReturn(entity).when(reviewMapperMock).toEntity(review);
//
//        when(reviewRepoMock.save(entity)).thenThrow(new RuntimeException("DB failure"));
//
//        doNothing().when(reviewServiceMock).rollbackS3Uploads(uploadedKeys);
//
//        RuntimeException ex = assertThrows(RuntimeException.class, () ->
//                reviewServiceMock.createReview(review, images)
//        );
//
//        assertTrue(ex.getMessage().contains("Failed to create review"));
//        assertTrue(ex.getCause().getMessage().contains("DB failure"));
//
//        verify(reviewServiceMock).rollbackS3Uploads(uploadedKeys);
//
//        verify(reviewMapperMock, never()).toDomain(any());
//    }

    //UPDATE REVIEW
    @Test
    void updateReview_ThrowsException_WhenIdIsNull() throws Exception {
        Review review = new Review();
        review.setId(null);

        assertThrows(IllegalArgumentException.class, () ->
                reviewServiceMock.updateReview(review, List.of(), List.of())
        );

        verifyNoInteractions(reviewRepoMock, reviewMapperMock, s3ServiceMock);
    }


    @Test
    void updateReview_ThrowsException_WhenReviewNotFound() throws Exception {
        Review review = new Review();
        review.setId(10L);
        review.setRouteId(1L);

        when(reviewRepoMock.getReviewsByRoute(1L)).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () ->
                reviewServiceMock.updateReview(review, List.of(), List.of())
        );

        verify(reviewRepoMock).getReviewsByRoute(1L);
        verifyNoInteractions(reviewMapperMock, s3ServiceMock);
    }

//    @Test
//    void updateReview_KeepsSpecifiedExistingImages() throws Exception {
//        Review review = new Review();
//        review.setId(1L);
//        review.setRouteId(1L);
//
//        ReviewImageEntity img1 = new ReviewImageEntity();
//        img1.setImageUrl("key1");
//
//        ReviewImageEntity img2 = new ReviewImageEntity();
//        img2.setImageUrl("key2");
//
//        ReviewEntity existing = new ReviewEntity();
//        existing.setId(1L);
//        existing.setImages(new ArrayList<>(List.of(img1, img2)));
//
//        when(reviewRepoMock.getReviewsByRoute(1L)).thenReturn(List.of(existing));
//
//        doReturn("key1").when(reviewServiceMock).extractS3Key("https://domain/key1");
//
//        List<String> existingUrls = List.of("https://domain/key1");
//
//        ReviewEntity saved = new ReviewEntity();
//        saved.setImages(existing.getImages());
//        when(reviewRepoMock.save(any())).thenReturn(saved);
//        when(reviewMapperMock.toDomain(saved)).thenReturn(new Review());
//
//        reviewServiceMock.updateReview(review, List.of(), existingUrls);
//
//        assertEquals(1, existing.getImages().size());
//        assertEquals("key1", existing.getImages().get(0).getImageUrl());
//    }


    @Test
    void updateReview_RemovesAllImages_WhenNoExistingUrlsProvided() throws Exception {
        Review review = new Review();
        review.setId(1L);
        review.setRouteId(1L);

        ReviewImageEntity img1 = new ReviewImageEntity();
        img1.setImageUrl("key1");

        ReviewEntity existing = new ReviewEntity();
        existing.setId(1L);
        existing.setImages(new ArrayList<>(List.of(img1)));

        when(reviewRepoMock.getReviewsByRoute(1L)).thenReturn(List.of(existing));

        ReviewEntity saved = new ReviewEntity();
        saved.setImages(new ArrayList<>());
        when(reviewRepoMock.save(any())).thenReturn(saved);
        when(reviewMapperMock.toDomain(saved)).thenReturn(new Review());

        reviewServiceMock.updateReview(review, List.of(), List.of());

        assertTrue(existing.getImages().isEmpty());
    }

    @Test
    void updateReview_AddsNewImages() throws Exception {
        Review review = new Review();
        review.setId(1L);
        review.setRouteId(1L);

        ReviewEntity existing = new ReviewEntity();
        existing.setId(1L);
        existing.setImages(new ArrayList<>());

        when(reviewRepoMock.getReviewsByRoute(1L)).thenReturn(List.of(existing));

        MultipartFile img = mock(MultipartFile.class);
        when(img.isEmpty()).thenReturn(false);
        when(s3ServiceMock.uploadImage(anyString(), eq(img))).thenReturn("newKey");

        ReviewEntity saved = new ReviewEntity();
        saved.setImages(existing.getImages());
        when(reviewRepoMock.save(any())).thenReturn(saved);
        when(reviewMapperMock.toDomain(saved)).thenReturn(new Review());

        reviewServiceMock.updateReview(review, List.of(img), List.of());

        assertEquals(1, existing.getImages().size());
        assertEquals("newKey", existing.getImages().get(0).getImageUrl());
    }

    @Test
    void updateReview_ReturnsDomainReview_WhenSuccessful() throws Exception {
        Review review = new Review();
        review.setId(1L);
        review.setRouteId(1L);

        ReviewEntity existing = new ReviewEntity();
        existing.setId(1L);
        existing.setImages(new ArrayList<>());

        when(reviewRepoMock.getReviewsByRoute(1L)).thenReturn(List.of(existing));

        ReviewEntity saved = new ReviewEntity();
        when(reviewRepoMock.save(existing)).thenReturn(saved);

        Review mapped = new Review();
        when(reviewMapperMock.toDomain(saved)).thenReturn(mapped);

        Review result = reviewServiceMock.updateReview(review, List.of(), List.of());

        assertEquals(mapped, result);
    }

    @Test
    void updateReview_ThrowsRuntimeException_OnUnexpectedError() throws Exception {
        Review review = new Review();
        review.setId(1L);
        review.setRouteId(1L);

        when(reviewRepoMock.getReviewsByRoute(1L))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                reviewServiceMock.updateReview(review, List.of(), List.of())
        );

        assertTrue(ex.getMessage().contains("Unexpected error while updating review"));
    }

    //DELETE REVIEW
    @Test
    void deleteReview_DeletesImagesFromS3_WhenReviewHasImages() {
        Long reviewId = 1L;

        ReviewImageEntity img1 = new ReviewImageEntity();
        img1.setImageUrl("key1");

        ReviewImageEntity img2 = new ReviewImageEntity();
        img2.setImageUrl("key2");

        ReviewEntity review = new ReviewEntity();
        review.setId(reviewId);
        review.setImages(List.of(img1, img2));

        when(reviewRepoMock.existsById(reviewId)).thenReturn(true);
        when(reviewRepoMock.getReviewsByRoute(null)).thenReturn(List.of(review));

        reviewServiceMock.deleteReview(reviewId);

        // Verify S3 deletions
        verify(s3ServiceMock).deleteImage("key1");
        verify(s3ServiceMock).deleteImage("key2");

        // Verify review removal
        verify(reviewRepoMock).deleteById(reviewId);
    }



    @Test
    void deleteReview_DoesNotCallS3_WhenNoImages() {
        Long reviewId = 1L;

        ReviewEntity review = new ReviewEntity();
        review.setId(reviewId);
        review.setImages(null);

        when(reviewRepoMock.existsById(reviewId)).thenReturn(true);
        when(reviewRepoMock.getReviewsByRoute(null)).thenReturn(List.of(review));

        reviewServiceMock.deleteReview(reviewId);

        verify(s3ServiceMock, never()).deleteImage(anyString());
        verify(reviewRepoMock).deleteById(reviewId);
    }

    @Test
    void deleteReview_ContinuesDeletingReview_WhenS3DeletionFails() {
        Long reviewId = 1L;

        ReviewImageEntity img1 = new ReviewImageEntity();
        img1.setImageUrl("key1");

        ReviewEntity review = new ReviewEntity();
        review.setId(reviewId);
        review.setImages(List.of(img1));

        when(reviewRepoMock.existsById(reviewId)).thenReturn(true);
        when(reviewRepoMock.getReviewsByRoute(null)).thenReturn(List.of(review));

        doThrow(new RuntimeException("S3 failure"))
                .when(s3ServiceMock).deleteImage("key1");

        reviewServiceMock.deleteReview(reviewId);

        verify(reviewRepoMock).deleteById(reviewId);
    }


    @Test
    void deleteReview_DeletesEvenIfReviewNotFound() {
        Long reviewId = 1L;

        when(reviewRepoMock.existsById(reviewId)).thenReturn(true);
        when(reviewRepoMock.getReviewsByRoute(null)).thenReturn(List.of());

        reviewServiceMock.deleteReview(reviewId);

        verify(s3ServiceMock, never()).deleteImage(anyString());

        verify(reviewRepoMock).deleteById(reviewId);
    }


    @Test
    void deleteReview_ThrowsRuntimeException_WhenUnexpectedErrorOccurs() {
        Long reviewId = 1L;

        // Ensure deleteReview() proceeds beyond the existence check
        when(reviewRepoMock.existsById(reviewId)).thenReturn(true);

        // Cause the unexpected error
        when(reviewRepoMock.getReviewsByRoute(null))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                reviewServiceMock.deleteReview(reviewId)
        );

        assertTrue(ex.getMessage().contains("Failed to delete review"));
    }


    //PROTECTED METHODS
    @Test
    void extractS3Key_ShouldReturnKeyFromFullPresignedUrl() {
        String url =
                "https://mybucket.s3.amazonaws.com/reviews/10/image1.png?AWSAccessKeyId=ABC&Expires=123";

        String result = reviewServiceMock.extractS3Key(url);

        assertEquals("reviews/10/image1.png", result);
    }

    @Test
    void extractS3Key_ShouldReturnKey_WhenNoQueryParameters() {
        String url = "https://mybucket.s3.amazonaws.com/reviews/10/photo.jpg";

        String result = reviewServiceMock.extractS3Key(url);

        assertEquals("reviews/10/photo.jpg", result);
    }

    @Test
    void extractS3Key_ShouldReturnOriginal_WhenNotAUrl() {
        String url = "reviews/10/plain-key.jpg";

        String result = reviewServiceMock.extractS3Key(url);

        assertEquals("reviews/10/plain-key.jpg", result);
    }

    @Test
    void extractS3Key_ShouldReturnOriginal_WhenAmazonawsNotPresent() {
        String url = "https://example.com/something/else.jpg";

        String result = reviewServiceMock.extractS3Key(url);

        assertEquals(url, result);
    }

    @Test
    void extractS3Key_ShouldHandleMalformedUrlGracefully() {
        String url = "https://amazonaws.com"; // no path after domain

        String result = reviewServiceMock.extractS3Key(url);

        assertEquals("https://amazonaws.com", result);
    }

    @Test
    void rollbackS3Uploads_ShouldDeleteAllKeys() {
        List<String> keys = List.of("img1.png", "img2.jpg");

        reviewServiceMock.rollbackS3Uploads(keys);

        verify(s3ServiceMock).deleteImage("img1.png");
        verify(s3ServiceMock).deleteImage("img2.jpg");

        verifyNoMoreInteractions(s3ServiceMock);
    }

    @Test
    void rollbackS3Uploads_ShouldCatchExceptions_AndContinue() {
        List<String> keys = List.of("img1.png", "img2.jpg");

        doThrow(new RuntimeException("S3 delete failed"))
                .when(s3ServiceMock)
                .deleteImage("img1.png");

        doNothing().when(s3ServiceMock).deleteImage("img2.jpg");

        reviewServiceMock.rollbackS3Uploads(keys);

        verify(s3ServiceMock).deleteImage("img1.png");
        verify(s3ServiceMock).deleteImage("img2.jpg");
    }


    @Test
    void uploadImages_ShouldReturnEmptyList_WhenImagesIsNull() throws IOException {
        List<String> result = reviewServiceMock.uploadImages(10L, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(s3ServiceMock);
    }

    @Test
    void uploadImages_ShouldUploadOnlyNonEmptyFiles() throws IOException {
        when(file1.isEmpty()).thenReturn(false);
        when(file2.isEmpty()).thenReturn(false);
        when(emptyFile.isEmpty()).thenReturn(true);

        when(s3ServiceMock.uploadImage(eq("reviews/10"), eq(file1))).thenReturn("key1");
        when(s3ServiceMock.uploadImage(eq("reviews/10"), eq(file2))).thenReturn("key2");

        List<String> result = reviewServiceMock.uploadImages(10L, List.of(file1, emptyFile, file2));

        assertEquals(List.of("key1", "key2"), result);

        verify(s3ServiceMock).uploadImage("reviews/10", file1);
        verify(s3ServiceMock).uploadImage("reviews/10", file2);
        verify(s3ServiceMock, never()).uploadImage(anyString(), eq(emptyFile));
    }

    @Test
    void validateRouteId_success_whenRouteExists() {
        Long routeId = 5L;

        when(routeRepoMock.existsById(routeId)).thenReturn(true);

        assertDoesNotThrow(() -> reviewServiceMock.validateRouteId(routeId));
    }

    @Test
    void extractS3Key_returnsOriginalUrl_whenUrlIsNullAndExceptionThrown() {
        String result = reviewServiceMock.extractS3Key(null);

        assertNull(result);
    }

    @Test
    void validateRouteId_throwsException_whenIdIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> reviewServiceMock.validateRouteId(null)
        );

        assertTrue(ex.getMessage().contains("Invalid route ID"));
    }

    @Test
    void validateRouteId_throwsException_whenIdIsZeroOrNegative() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> reviewServiceMock.validateRouteId(0L)
        );

        assertTrue(ex.getMessage().contains("Invalid route ID"));
    }

    @Test
    void validateRouteId_throwsException_whenRouteDoesNotExist() {
        when(routeRepoMock.existsById(5L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> reviewServiceMock.validateRouteId(5L)
        );

        assertTrue(ex.getMessage().contains("Route not found"));
    }

    @Test
    void validateRouteId_passes_whenRouteExists() {
        when(routeRepoMock.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> reviewServiceMock.validateRouteId(1L));
    }


}
package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
public class S3ServiceImpl implements S3Service {
    private final S3Presigner presigner;
    private final S3Client s3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3ServiceImpl(S3Presigner presigner, S3Client s3) {
        this.presigner = presigner;
        this.s3 = s3;
    }

    public String getPreSignedUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1))
                .getObjectRequest(getObjectRequest)
                .build();

        log.info("[SERVICE] Getting presigned URL for key: {}", key);

        return presigner.presignGetObject(presignRequest)
                .url()
                .toString();
    }

    @Override
    public String uploadImage(String folder, MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                : "";

        String key = folder + "/" + UUID.randomUUID() + extension;
        log.info("[SERVICE] Uploading image to S3: {}", key);

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return key;
    }


    @Override
    public void deleteImage(String key) {
        try {
            s3.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            log.debug("[SERVICE] Deleted image with key={}", key);
        } catch (SdkClientException e) {
            log.error("[SERVICE] SDK client error deleting image with key={}: {}", key, e.getMessage(), e);
            throw e;
        }
    }

}

package com.digicompass.backend.application.interfaces;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface S3Service {
    String getPreSignedUrl(String key);
    String uploadImage(String folder, MultipartFile file) throws IOException;
    void deleteImage(String key);
    List<String> uploadImages(Long routeId, List<MultipartFile> images) throws IOException;
    void rollbackS3Uploads(List<String> keys);
    void rollbackS3Upload(String keys);
    String uploadImage(Long userId, MultipartFile image) throws IOException;

}

package com.digicompass.backend.application.interfaces;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface S3Service {
    String getPreSignedUrl(String key);
    String uploadImage(String folder, MultipartFile file) throws IOException;
    void deleteImage(String key);
}

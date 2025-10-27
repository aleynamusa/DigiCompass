package com.digicompass.backend.application.interfaces;

import org.springframework.stereotype.Service;

@Service
public interface S3Service {
    String getPreSignedUrl(String key);

}

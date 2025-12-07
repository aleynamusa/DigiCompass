package com.digicompass.backend.controller.mapper;


import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class MultipartFileMapper {

    public List<String> multipartFilesToStrings(List<MultipartFile> files) {
        if (files == null) return null;
        return files.stream()
                .map(MultipartFile::getOriginalFilename)
                .toList();
    }
}

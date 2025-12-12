package com.digicompass.backend.controller.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MultipartFileMapper {

    @Named("multipartFilesToStrings")
    public List<String> multipartFilesToStrings(List<MultipartFile> files) {
        if (files == null) Collections.emptyList();
        return files.stream()
                .map(MultipartFile::getOriginalFilename)
                .toList();
    }
}

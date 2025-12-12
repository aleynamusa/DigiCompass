package com.digicompass.backend.controller.dto.request;

import com.digicompass.backend.controller.dto.UserDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReviewRequestDto {
    private Long id;

    private String review;

    private UserDto userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<MultipartFile> images;
    private String existingImageUrls;

    private Long routeId;

    public List<String> getExistingImageUrlsList() {
        if (existingImageUrls == null || existingImageUrls.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(existingImageUrls,
                    new TypeReference<List<String>>() {});
        } catch (Exception _) {
            return new ArrayList<>();
        }
    }

}

package com.digicompass.backend.presentation.controller.dto;

import com.digicompass.backend.application.models.User;
import com.digicompass.backend.domain.entity.UserEntity;
import com.digicompass.backend.application.models.RouteImage;
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
public class ReviewDto {
    private Long id;

    private String review;

    private UserDto userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<MultipartFile> images = new ArrayList<>();

    private Long routeId;

}

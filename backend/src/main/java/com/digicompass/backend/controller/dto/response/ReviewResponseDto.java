package com.digicompass.backend.controller.dto.response;

import com.digicompass.backend.controller.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDto {
    private Long id;

    private String review;

    private UserDto userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<String> images = new ArrayList<>();

    private Long routeId;
}

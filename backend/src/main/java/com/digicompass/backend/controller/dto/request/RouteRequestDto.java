package com.digicompass.backend.controller.dto.request;


import com.digicompass.backend.types.Difficulty;
import com.digicompass.backend.types.RouteType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequestDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 80, message = "Name must be between 2 and 80 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(min = 3, max = 255, message = "Description must be between 3 and 255 characters")
    private String description;

    @NotNull(message = "Route type must be specified")
    private RouteType routeType;

    @NotNull(message = "Difficulty level must be specified")
    private Difficulty difficulty;


    @PositiveOrZero(message = "Distance must be zero or positive")
    private float distance;

    private String duration;
    private String geometry;
    private List<MultipartFile> images;


}

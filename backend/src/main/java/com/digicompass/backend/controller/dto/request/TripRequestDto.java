package com.digicompass.backend.controller.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TripRequestDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 80, message = "Name must be between 2 and 80 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(min = 3, max = 255, message = "Description must be between 3 and 255 characters")
    private String description;

    @FutureOrPresent(message = "Planned date must be today or in the future")
    private LocalDateTime plannedDate;

    @Positive(message = "Route Id must be positive")
    private Long routeId;

    @NotNull(message = "Accessibility is required")
    private Boolean accessibility;

}

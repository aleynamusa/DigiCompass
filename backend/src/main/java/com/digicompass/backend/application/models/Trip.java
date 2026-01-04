package com.digicompass.backend.application.models;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trip {

    private Long id;

    @NotBlank
    @Size(min = 2, max = 80)
    private String name;

    @NotBlank
    @Size(min = 3, max = 255)
    private String description;

    @FutureOrPresent(message = "Planned date must be today or in the future")
    private LocalDateTime plannedDate;

    @Positive(message = "Route Id must be positive")
    private Long routeId;

    @NotNull(message = "Accessibility is required")
    private Boolean accessibility;

    @Positive(message = "User Id must be positive")
    private Long userId;
}


package com.digicompass.backend.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    @Positive(message = "UserId must be positive")
    @NotNull(message = "UserId cannot be null")
    private Long id;

    @NotBlank(message = "Username cannot be blank")
    private String username;
}

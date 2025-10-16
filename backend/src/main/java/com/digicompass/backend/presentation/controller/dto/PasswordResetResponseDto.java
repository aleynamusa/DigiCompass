package com.digicompass.backend.presentation.controller.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetResponseDto {
    private boolean success;
    private String message;


    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
}

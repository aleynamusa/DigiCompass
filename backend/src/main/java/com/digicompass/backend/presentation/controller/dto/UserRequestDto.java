package com.digicompass.backend.presentation.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    private Long id;
    private String username;
    private String email;
    private byte age;
    private String password;
    private String token;
}

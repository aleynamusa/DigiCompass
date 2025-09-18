package com.digicompass.backend.infrastucture.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    private String id;
    private String username;
    private String email;
    private byte age;
    private String password;

}

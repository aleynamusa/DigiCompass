package com.digicompass.backend.infrastucture.persistence.models;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class  User {
    private Long id;
    private String username;
    private String email;
    private byte age;
    private String password;
//    private String resetToken;

}

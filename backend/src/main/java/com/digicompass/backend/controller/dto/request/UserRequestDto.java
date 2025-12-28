package com.digicompass.backend.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    @Positive(message = "UserId must be positive")
    private Long id;

    @NotBlank(message = "Username cannot be blank")
    private String username;

    @Email(message = "Please write a valid email address")
    @NotBlank(message = "Email cannot be  blank")
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Pattern(regexp="^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", message = "Please be sure that you have at least one Upper Case Letter, Lower Case Letter, Digit and Special Symbol")
    private String password;

    private Long role;

}

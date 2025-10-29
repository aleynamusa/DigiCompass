package com.digicompass.backend.application.models;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class  User {
    private Long id;
    private String username;
    private String email;
    private LocalDate birthDate;
    private String password;

    public int CalculateAge(){
        Period period = Period.between(birthDate, LocalDate.now());

        return period.getYears();
    }
}

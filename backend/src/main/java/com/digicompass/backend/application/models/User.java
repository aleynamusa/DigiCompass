package com.digicompass.backend.application.models;

import lombok.*;

import java.time.LocalDate;
import java.time.Period;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    private Long id;
    private String username;
    private String email;
    private LocalDate birthDate;
    private String password;
    private Long role_id;

    public int CalculateAge(){
        Period period = Period.between(birthDate, LocalDate.now());

        return period.getYears();
    }

    public User(Long id, String username){
        this.id = id;
        this.username = username;
    }
}

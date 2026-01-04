package com.digicompass.backend.application.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @NotNull(message = "User id is required")
    @Positive(message = "User id must be positive")
    private Long id;
    private String username;
    private String email;
    private LocalDate birthDate;
    private String password;
    private Long roleId;
    private String imageUrl;
    private String bio;
    private boolean isPublicProfile;


    public int calculateAge(){
        Period period = Period.between(birthDate, LocalDate.now());

        return period.getYears();
    }

    public User(Long id, String username){
        this.id = id;
        this.username = username;
    }
}

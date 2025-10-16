package com.digicompass.backend.infrastucture.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String username;

    @Column(nullable=false, unique=true)
    private String email;

    @Column(nullable=false)
    private Integer age;

    @Column(nullable=false)
    private String password;

//    private String resetToken;
//
//    @Column(name = "reset_token_expiry")
//    private LocalDateTime resetTokenExpiry;
}
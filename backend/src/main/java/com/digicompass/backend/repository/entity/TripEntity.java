package com.digicompass.backend.repository.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "trip")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    @Size(min = 2, max = 80)
    private String name;

    @Column(nullable = false)
    @NotBlank
    @Size(min = 3, max = 255)
    private String description;

    @Column(nullable = false, name = "planned_date")
    @FutureOrPresent(message = "Planned date must be today or in the future")
    private LocalDateTime plannedDate;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private RouteEntity route;

    @Column(nullable = false)
    private Boolean accessibility;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}

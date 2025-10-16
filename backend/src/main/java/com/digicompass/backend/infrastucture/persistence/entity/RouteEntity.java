package com.digicompass.backend.infrastucture.persistence.entity;

import com.sun.tools.xjc.model.CDefaultValue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "routes")
@Data
@NoArgsConstructor
public class RouteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private String routeType;   //HIKING, CYCLING, RUNNING, WALKING

    @Column(nullable = false)
    private String difficulty;  //BEGINNER, EASY, MODERATE, HARD, EXPERT, EXTREME

    @Column(nullable = false)
    private float distance;

    @Column(nullable = false)
    private String duration;

    @Column(nullable = false)
    private String startLocation;

    @Column(nullable = false)
    private String endLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private UserEntity createdByUserId;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}


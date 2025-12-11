package com.digicompass.backend.repository.entity;

import com.digicompass.backend.types.Difficulty;
import com.digicompass.backend.types.RouteType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.Geometry;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RouteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private RouteType routeType; //HIKING, CYCLING, RUNNING, WALKING

    @Column(nullable = false)
    private Difficulty difficulty; //EASY, MEDIUM, HARD

    @Column(nullable = false)
    private float distance;

    @Column(nullable = false)
    private String duration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private UserEntity createdByUserId;

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @JsonIgnore
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "route_geometry", columnDefinition = "geometry")
    private Geometry routeGeometry;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RouteImageEntity> images = new ArrayList<>();

    @OneToMany(mappedBy = "routeId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ReviewEntity> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "routeId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RatingEntity> ratings = new ArrayList<>();

}





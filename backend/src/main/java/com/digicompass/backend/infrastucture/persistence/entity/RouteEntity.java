package com.digicompass.backend.infrastucture.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Geometry;
import java.time.LocalDateTime;

@Entity
@Table(name = "routes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private String routeType; //HIKING, CYCLING, RUNNING, WALKING

    @Column(nullable = false)
    private String difficulty; //BEGINNER, EASY, MODERATE, HARD, EXPERT, EXTREME

    @Column(nullable = false)
    private float distance;

    @Column(nullable = false)
    private String duration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private UserEntity createdByUserId;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @JsonIgnore
    @Column(name = "route_geometry", columnDefinition = "geometry")
    private Geometry routeGeometry;

//    @Column()
//    private List<String> images;

}





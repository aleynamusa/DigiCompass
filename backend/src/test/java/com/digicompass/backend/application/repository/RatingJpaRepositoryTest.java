//package com.digicompass.backend.application.repository;
//
//import com.digicompass.backend.repository.entity.*;
//import com.digicompass.backend.repository.repositories.RatingJpaRepository;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import jakarta.persistence.*;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.locationtech.jts.geom.Geometry;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@ExtendWith(SpringExtension.class)
//@DataJpaTest
//public class RatingJpaRepositoryTest {
//    @Autowired
//    private EntityManager entityManager;
//    @Autowired
//    private RatingJpaRepository ratingRepo;
//
//    public class RatingEntity {
//        @Id
//        @GeneratedValue(strategy = GenerationType.IDENTITY)
//        private Long id;
//
//        @Column(nullable = false)
//        private Double rating; // 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0
//
//        @ManyToOne(fetch = FetchType.LAZY)
//        @JoinColumn(name = "created_by_user_id", nullable = false)
//        private UserEntity userId;
//
//        @CreationTimestamp
//        @Column(nullable = false, updatable = false)
//        private LocalDateTime createdAt;
//
//        @UpdateTimestamp
//        @Column(nullable = false)
//        private LocalDateTime updatedAt;
//
//        @ManyToOne()
//        @JoinColumn(name = "route_id", nullable = false)
//        private RouteEntity routeId;
//
//    }
//
//    public class RouteEntity {
//        @Id
//        @GeneratedValue(strategy = GenerationType.IDENTITY)
//        private Long id;
//
//        @Column(nullable=false, unique=true)
//        private String name;
//
//        @Column
//        private String description;
//
//        @Column(nullable = false)
//        private String routeType; //HIKING, CYCLING, RUNNING, WALKING
//
//        @Column(nullable = false)
//        private String difficulty; //BEGINNER, EASY, MODERATE, HARD, EXPERT, EXTREME
//
//        @Column(nullable = false)
//        private float distance;
//
//        @Column(nullable = false)
//        private String duration;
//
//        @ManyToOne(fetch = FetchType.LAZY)
//        @JoinColumn(name = "created_by_user_id", nullable = false)
//        private UserEntity createdByUserId;
//
//        @CreationTimestamp
//        @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
//        private LocalDateTime createdAt;
//
//        @UpdateTimestamp
//        @Column(nullable = false)
//        private LocalDateTime updatedAt;
//
//        @JsonIgnore
//        @Basic(fetch = FetchType.LAZY)
//        @Column(name = "route_geometry", columnDefinition = "geometry")
//        private Geometry routeGeometry;
//
//        @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
//        @JsonManagedReference
//        private List<RouteImageEntity> images = new ArrayList<>();
//
//        @OneToMany(mappedBy = "routeId", cascade = CascadeType.ALL, orphanRemoval = true)
//        @JsonManagedReference
//        private List<ReviewEntity> reviews = new ArrayList<>();
//
//        @OneToMany(mappedBy = "routeId", cascade = CascadeType.ALL, orphanRemoval = true)
//        @JsonManagedReference
//        private List<com.digicompass.backend.repository.entity.RatingEntity> ratings = new ArrayList<>();
//
//    }
//
//    @Test
//    void save_shouldSaveRatingWithAllFields() {
//
//        RoleEntity role = saveRole("User");
//        UserEntity user = saveUser("test", "test@gmail.com", LocalDate.of(2005, 5, 5), "testPass123@", role, "image1");
//        RouteEntity route
//        RatingEntity student = new RatingEntity(0.5d, user, LocalDateTime.now(), LocalDateTime.now(), )
//        StudentEntity savedStudent = studentRepository.save(student);
//        assertNotNull(savedStudent.getId());
//        savedStudent = entityManager.find(StudentEntity.class,
//                savedStudent.getId());
//        StudentEntity expectedStudent = StudentEntity.builder()
//                .id(savedStudent.getId())
//                .pcn(111L)
//                .name("Ronaldo Nazario")
//                .country(brazil)
//                .build();
//        assertEquals(expectedStudent, savedStudent);
//    }
//    private CountryEntity saveCountry(String name, String code) {
//        CountryEntity country =
//                CountryEntity.builder().name(name).code(code).build();
//        entityManager.persist(country);
//        return country;
//    }
//}
//
//}

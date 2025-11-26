//package com.digicompass.backend.application.repository;
//
//import com.digicompass.backend.repository.entity.RatingEntity;
//import com.digicompass.backend.repository.entity.RouteEntity;
//import com.digicompass.backend.repository.entity.UserEntity;
//import com.digicompass.backend.repository.repositories.RatingJpaRepository;
//import jakarta.persistence.*;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.time.LocalDateTime;
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
//
//    }
//
//    @Test
//    void save_shouldSaveRatingWithAllFields() {
//        CountryEntity brazil = saveCountry("Brazil", "BR");
//        RatingEntity student = new RatingEntity()
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

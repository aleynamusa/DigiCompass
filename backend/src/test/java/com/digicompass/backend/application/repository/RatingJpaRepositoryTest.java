//package com.digicompass.backend.application.repository;
//
//import com.digicompass.backend.repository.repositories.RatingJpaRepository;
//import jakarta.persistence.EntityManager;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//@ExtendWith(SpringExtension.class)
//@DataJpaTest
//public class RatingJpaRepositoryTest {
//    @Autowired
//    private EntityManager entityManager;
//    @Autowired
//    private RatingJpaRepository ratingRepo;
//
//    @Test
//    void save_shouldSaveRatingWithAllFields() {
//        CountryEntity brazil = saveCountry("Brazil", "BR");
//        StudentEntity student = StudentEntity.builder()
//                .pcn(111L)
//                .name("Ronaldo Nazario")
//                .country(brazil)
//                .build();
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

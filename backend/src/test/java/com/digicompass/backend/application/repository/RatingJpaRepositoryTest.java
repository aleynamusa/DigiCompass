package com.digicompass.backend.application.repository;

import com.digicompass.backend.repository.entity.*;
import com.digicompass.backend.repository.repositories.RatingJpaRepository;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create"
})

public class RatingJpaRepositoryTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private RatingJpaRepository ratingRepo;


    @Test
    void shouldSaveRatingCorrectly() {
        RoleEntity role = new RoleEntity();
        role.setRole("USER");
        entityManager.persist(role);

        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("email@test.com");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setPassword("secret");
        user.setRole(role);
        entityManager.persist(user);

        RouteEntity route = new RouteEntity();
        route.setName("Test Route");
        route.setDescription("desc");
        route.setRouteType("HIKING");
        route.setDifficulty("EASY");
        route.setDistance(5f);
        route.setDuration("1h");
        route.setCreatedByUserId(user);
        entityManager.persist(route);

        RatingEntity rating = new RatingEntity();
        rating.setRating(4.5);
        rating.setUserId(user);
        rating.setRouteId(route);

        RatingEntity saved = ratingRepo.save(rating);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getRating()).isEqualTo(4.5);
        assertThat(saved.getUserId().getUsername()).isEqualTo("testuser");
        assertThat(saved.getRouteId().getName()).isEqualTo("Test Route");
    }
}



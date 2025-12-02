package com.digicompass.backend.application.repository;

import com.digicompass.backend.repository.entity.*;
import com.digicompass.backend.repository.repositories.RatingJpaRepository;
import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(SpringExtension.class) //H2 database - in memory database(stores in RAM)
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

    UserEntity firstUser;
    UserEntity secondUser;
    RouteEntity route;
    RatingEntity firstRating;
    RatingEntity secondRating;
    RatingEntity firstSaved;
    RatingEntity secondSaved;

    @BeforeEach
    void setup() {
        RoleEntity role = new RoleEntity();
        role.setRole("USER");
        entityManager.persist(role);

        firstUser = new UserEntity();
        firstUser.setUsername("testuser");
        firstUser.setEmail("email@test.com");
        firstUser.setBirthDate(LocalDate.of(1990, 1, 1));
        firstUser.setPassword("secret");
        firstUser.setRole(role);
        entityManager.persist(firstUser);

        secondUser = new UserEntity();
        secondUser.setUsername("testuser1");
        secondUser.setEmail("email1@test.com");
        secondUser.setBirthDate(LocalDate.of(1990, 1, 1));
        secondUser.setPassword("secret");
        secondUser.setRole(role);
        entityManager.persist(secondUser);

        route = new RouteEntity();
        route.setName("Test Route");
        route.setDescription("desc");
        route.setRouteType("HIKING");
        route.setDifficulty("EASY");
        route.setDistance(5f);
        route.setDuration("1h");
        route.setCreatedByUserId(firstUser);
        entityManager.persist(route);

        firstRating = new RatingEntity();
        firstRating.setRating(4.0);
        firstRating.setUserId(firstUser);
        firstRating.setRouteId(route);

        secondRating = new RatingEntity();
        secondRating.setRating(3.0);
        secondRating.setUserId(firstUser);
        secondRating.setRouteId(route);

        firstSaved = ratingRepo.save(firstRating);
        secondSaved = ratingRepo.save(secondRating);
    }

    @Test
    void shouldSaveRatingCorrectly() {

        assertThat(firstSaved.getId()).isNotNull();
        assertThat(firstSaved.getRating()).isEqualTo(4.0);
        assertThat(firstSaved.getUserId().getUsername()).isEqualTo("testuser");
        assertThat(firstSaved.getRouteId().getName()).isEqualTo("Test Route");
    }

    @Test
    void shouldReturnAvgRatingCorrectly(){
        assertThat(firstSaved.getRating()).isEqualTo(firstRating.getRating());
        assertThat(secondSaved.getRating()).isEqualTo(secondRating.getRating());
        assertThat(ratingRepo.getAvgRatingByRoute(route.getId()).equals(3.5)).isTrue();
    }

    @Test
    void shouldGetRatingsByRouteCorrectly(){
        assertThat(ratingRepo.getRatingsByRoute(route.getId())).isEqualTo(List.of(firstRating, secondRating));

    }

}



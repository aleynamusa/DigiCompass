package com.digicompass.backend.application.repository;

import com.digicompass.backend.repository.entity.ReviewEntity;
import com.digicompass.backend.repository.entity.RoleEntity;
import com.digicompass.backend.repository.entity.RouteEntity;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.ReviewJpaRepository;
import com.digicompass.backend.types.Difficulty;
import com.digicompass.backend.types.RouteType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create"
})
class ReviewJpaRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ReviewJpaRepository reviewRepo;

    UserEntity firstUser;
    UserEntity secondUser;
    RouteEntity route;
    ReviewEntity firstReview;
    ReviewEntity secondReview;
    ReviewEntity firstSaved;
    ReviewEntity secondSaved;

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
        route.setRouteType(RouteType.Cycling);
        route.setDifficulty(Difficulty.Easy);
        route.setDistance(5f);
        route.setDuration("1h");
        route.setCreatedByUserId(firstUser);
        entityManager.persist(route);

        firstReview = new ReviewEntity();
        firstReview.setUserId(firstUser);
        firstReview.setReview("review");
        firstReview.setRouteId(route);

        secondReview = new ReviewEntity();
        secondReview.setUserId(secondUser);
        secondReview.setReview("review");
        secondReview.setRouteId(route);

        firstSaved = reviewRepo.save(firstReview);
        secondSaved = reviewRepo.save(secondReview);
    }

    @Test
    void shouldSaveReviewCorrectly() {
        assertThat(firstSaved.getId()).isNotNull();
        assertThat(firstSaved.getUserId()).isNotNull();
        assertThat(firstSaved.getUserId().getUsername()).isEqualTo("testuser");
        assertThat(firstSaved.getRouteId()).isNotNull();
        assertThat(firstSaved.getRouteId().getName()).isEqualTo("Test Route");
    }

    @Test
    void shouldGetReviewsByRouteCorrectly() {
        List<ReviewEntity> reviews = reviewRepo.getReviewsByRoute(route.getId());

        assertThat(reviews).hasSize(2);

        assertThat(reviews).allSatisfy(r -> {
            assertThat(r.getRouteId()).isNotNull();
            assertThat(r.getRouteId().getId()).isEqualTo(route.getId());
            assertThat(r.getUserId()).isNotNull();
        });
    }

    @Test
    void shouldReturnEmptyListIfNoReviewsForRoute() {
        List<ReviewEntity> reviews = reviewRepo.getReviewsByRoute(999L);
        assertThat(reviews).isEmpty();
    }

    @Test
    void shouldSetTimestampsOnSave() {
        assertThat(firstSaved.getCreatedAt()).isNotNull();
        assertThat(firstSaved.getUpdatedAt()).isNotNull();
    }


}

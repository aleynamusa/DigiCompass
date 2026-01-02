package com.digicompass.backend.application.repository;

import com.digicompass.backend.repository.entity.*;
import com.digicompass.backend.repository.repositories.FavouriteRouteJpaRepository;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create"
})
class FavouriteRouteJpaRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private FavouriteRouteJpaRepository favouriteRouteRepo;

    private UserEntity user;
    private RouteEntity route1;
    private RouteEntity route2;

    @BeforeEach
    void setup() {
        // Create and persist a user
        user = new UserEntity();
        user.setUsername("user1");
        user.setEmail("user1@test.com");
        user.setBirthDate(LocalDate.of(1985, 5, 20));
        user.setPassword("password");
        entityManager.persist(user);

        // Create and persist two routes
        route1 = new RouteEntity();
        route1.setName("Route One");
        route1.setDescription("Desc 1");
        route1.setRouteType(RouteType.Cycling);
        route1.setDifficulty(Difficulty.Medium);
        route1.setDistance(10f);
        route1.setDuration("2h");
        route1.setCreatedByUserId(user);
        entityManager.persist(route1);

        route2 = new RouteEntity();
        route2.setName("Route Two");
        route2.setDescription("Desc 2");
        route2.setRouteType(RouteType.Running);
        route2.setDifficulty(Difficulty.Hard);
        route2.setDistance(15f);
        route2.setDuration("3h");
        route2.setCreatedByUserId(user);
        entityManager.persist(route2);

        // Persist FavouriteRouteEntities for user liking route1 and route2
        FavouriteRouteEntity fav1 = new FavouriteRouteEntity(user, route1);
        fav1.setCreatedAt(LocalDateTime.now());
        entityManager.persist(fav1);

        FavouriteRouteEntity fav2 = new FavouriteRouteEntity(user, route2);
        fav2.setCreatedAt(LocalDateTime.now());
        entityManager.persist(fav2);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void existsByIdUserIdAndIdRouteId_shouldReturnTrueIfExists() {
        boolean exists = favouriteRouteRepo.existsByIdUserIdAndIdRouteId(user.getId(), route1.getId());
        assertThat(exists).isTrue();

        boolean notExists = favouriteRouteRepo.existsByIdUserIdAndIdRouteId(user.getId(), 999L);
        assertThat(notExists).isFalse();
    }

    @Test
    void findAllLikedRoutesByUserId_shouldReturnAllRoutesLikedByUser() {
        List<RouteEntity> likedRoutes = favouriteRouteRepo.findAllLikedRoutesByUserId(user.getId());
        assertThat(likedRoutes).hasSize(2);
        assertThat(likedRoutes).extracting(RouteEntity::getName)
                .containsExactlyInAnyOrder("Route One", "Route Two");
    }
}

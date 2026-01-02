package com.digicompass.backend.application.repository;

import com.digicompass.backend.repository.entity.RouteEntity;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.RouteJpaRepository;
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
public class RouteJpaRepositoryTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RouteJpaRepository routeRepository;

    private UserEntity user;
    private RouteEntity route1, route2, route3;

    @BeforeEach
    void setup() {
        user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("testuser@test.com");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setPassword("secret");
        entityManager.persist(user);

        route1 = new RouteEntity();
        route1.setName("Mountain Trail");
        route1.setDescription("Challenging mountain trail");
        route1.setRouteType(RouteType.Hiking);
        route1.setDifficulty(Difficulty.Hard);
        route1.setDistance(20f);
        route1.setDuration("5h");
        route1.setCreatedByUserId(user);
        entityManager.persist(route1);

        route2 = new RouteEntity();
        route2.setName("City Cycling");
        route2.setDescription("Easy cycling around the city");
        route2.setRouteType(RouteType.Cycling);
        route2.setDifficulty(Difficulty.Easy);
        route2.setDistance(4f);
        route2.setDuration("1h");
        route2.setCreatedByUserId(user);
        entityManager.persist(route2);

        route3 = new RouteEntity();
        route3.setName("Country Run");
        route3.setDescription("Medium difficulty running route");
        route3.setRouteType(RouteType.Running);
        route3.setDifficulty(Difficulty.Medium);
        route3.setDistance(12f);
        route3.setDuration("1.5h");
        route3.setCreatedByUserId(user);
        entityManager.persist(route3);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testFindFiltered_byTypeDifficultyDistance() {
        // Filter by type = "CYCLING", difficulty = "EASY", distance = 4 (should match route2)
        List<RouteEntity> filteredRoutes = routeRepository.findFiltered("CYCLING", "EASY", 4f);
        assertThat(filteredRoutes).hasSize(1);
        assertThat(filteredRoutes.get(0).getName()).isEqualTo("City Cycling");

        // Filter by null type and difficulty, distance 12f (should match route3 based on distance range)
        filteredRoutes = routeRepository.findFiltered(null, null, 12f);
        assertThat(filteredRoutes).extracting(RouteEntity::getName)
                .contains("Country Run")
                .doesNotContain("Mountain Trail", "City Cycling");

        // Filter by difficulty HARD (should match route1)
        filteredRoutes = routeRepository.findFiltered(null, "HARD", null);
        assertThat(filteredRoutes).extracting(RouteEntity::getName)
                .containsExactly("Mountain Trail");
    }

    @Test
    void testGetAllIds() {
        List<Long> ids = routeRepository.getAllIds();
        assertThat(ids).containsExactlyInAnyOrder(route1.getId(), route2.getId(), route3.getId());
    }

    @Test
    void testGetRouteEntitiesByName() {
        List<RouteEntity> routes = routeRepository.getRouteEntitiesByName("City Cycling");
        assertThat(routes).hasSize(1);
        assertThat(routes.get(0).getDescription()).isEqualTo("Easy cycling around the city");
    }

    @Test
    void testFindAllByUserId() {
        List<RouteEntity> routes = routeRepository.findAllByUserId(user.getId());
        assertThat(routes).hasSize(3);
        assertThat(routes).extracting(RouteEntity::getCreatedByUserId)
                .allMatch(u -> u.getId().equals(user.getId()));
    }
}

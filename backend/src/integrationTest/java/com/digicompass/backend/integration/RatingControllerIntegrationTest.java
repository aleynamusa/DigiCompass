package com.digicompass.backend.controller;


import com.digicompass.backend.controller.dto.RatingDto;
import com.digicompass.backend.controller.dto.UserDto;
import com.digicompass.backend.repository.entity.RouteEntity;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = com.digicompass.backend.BackendApplication.class
)
@AutoConfigureMockMvc
@Testcontainers
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class RatingControllerIntegrationTest {

    private static final DockerImageName POSTGIS_IMAGE = DockerImageName
            .parse("postgis/postgis:17-3.5")
            .asCompatibleSubstituteFor("postgres");

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGIS_IMAGE)
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerPgProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);


        // For debugging
        registry.add("spring.jpa.show-sql", () -> "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userRepo;

    @Autowired
    private RouteJpaRepository routeRepo;

    @Autowired
    private javax.sql.DataSource ds;

    @BeforeEach
    void cleanDatabase() throws Exception {
        try (var conn = ds.getConnection();
             var st = conn.createStatement()) {
            st.execute("TRUNCATE TABLE rating CASCADE");
            st.execute("TRUNCATE TABLE review CASCADE");
            st.execute("TRUNCATE TABLE routes CASCADE");
            st.execute("TRUNCATE TABLE users CASCADE");

            st.execute("ALTER SEQUENCE routes_id_seq RESTART WITH 1");
            st.execute("ALTER SEQUENCE users_id_seq RESTART WITH 1");
            st.execute("ALTER SEQUENCE rating_id_seq RESTART WITH 1");
            st.execute("ALTER SEQUENCE review_id_seq RESTART WITH 1");
        }
    }


    private Long userId;
    private Long routeId;

    @BeforeEach
    void setup() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@mail.com");
        user.setBirthDate(LocalDate.of(1995, 1, 1));
        user.setPassword("1234");
        userRepo.save(user);
        userId = user.getId();

        RouteEntity route = new RouteEntity();
        route.setName("Sample Route");
        route.setDescription("desc");
        route.setRouteType("HIKING");
        route.setDifficulty("MODERATE");
        route.setDistance(5f);
        route.setDuration("30m");
        route.setCreatedByUserId(user);
        routeRepo.save(route);
        routeId = route.getId();
    }

    @Test
    void shouldCreateRating() throws Exception {
        RatingDto rating = new RatingDto();
        rating.setRating(4.0);
        rating.setUserId(new UserDto(userId, "testuser"));
        rating.setRouteId(routeId);

        mockMvc.perform(post("/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rating)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.rating").value(4.0));
    }

    @Test
    void shouldGetRatingsByRoute() throws Exception {
        shouldCreateRating();

        mockMvc.perform(get("/rating/route/" + routeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating").value(4.0));
    }

    @Test
    void shouldDeleteRating() throws Exception {
        // Create rating
        String response = mockMvc.perform(post("/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                   "rating": 3.5,
                                   "userId": {"id": 1, "username": "testuser"},
                                   "routeId": 1
                                }
                                """))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long ratingId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/rating/delete/" + ratingId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldUpdateRating() throws Exception {
        // 1. Create a rating
        RatingDto createDto = new RatingDto();
        createDto.setRating(3.0);
        createDto.setUserId(new UserDto(userId, "testuser"));
        createDto.setRouteId(routeId);

        String createResponse = mockMvc.perform(post("/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long ratingId = objectMapper.readTree(createResponse).get("id").asLong();

        // 2. Update the rating
        RatingDto updateDto = new RatingDto();
        updateDto.setRating(4.5); // new value
        updateDto.setUserId(new UserDto(userId, "testuser"));
        updateDto.setRouteId(routeId);

        mockMvc.perform(put("/rating/update/" + ratingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(4.5))
                .andExpect(jsonPath("$.id").value(ratingId));
    }

}

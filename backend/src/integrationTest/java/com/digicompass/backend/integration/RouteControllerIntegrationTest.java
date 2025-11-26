package com.digicompass.backend.integration;

import com.digicompass.backend.application.interfaces.RouteService;
import com.digicompass.backend.repository.entity.RouteEntity;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
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
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = com.digicompass.backend.BackendApplication.class
)
@AutoConfigureMockMvc
@Testcontainers
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")//escape the autowired bean warning
public class RouteControllerIntegrationTest {
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

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");

        registry.add("spring.jpa.show-sql", () -> "true");
    }

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private RouteJpaRepository routeRepository;

    @Autowired
    private UserJpaRepository userRepository;


    @BeforeEach
    void setup() {
        routeRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@mail.com");
        user.setBirthDate(LocalDate.of(1995, 1, 1));
        user.setPassword("1234");
        userRepository.save(user);

        RouteEntity r = new RouteEntity();
        r.setName("Test Route");
        r.setDescription("A sample route");
        r.setRouteType("HIKING");
        r.setDifficulty("EASY");
        r.setCreatedByUserId(user);
        r.setDistance(12.5f);
        r.setDuration("02:00");
        r.setCreatedAt(LocalDateTime.now());
        r.setUpdatedAt(LocalDateTime.now());

        routeRepository.save(r);
    }

    //fullstack integration database-> service -> controller flow
    @Test
    void shouldReturnListOfRoutes() throws Exception {
        mockMvc.perform(get("/route")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Route"));
    }

    @Test
    void shouldReturnListOfRoutesWhenFilteredByType() throws Exception{
        mockMvc.perform(get("/route/filter")
                        .param("type", "HIKING")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Route"));
    }

    @Test
    void shouldReturnListOfRoutesWhenFilteredByDifficulty() throws Exception{
        mockMvc.perform(get("/route/filter")
                        .param("difficulty", "EASY")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Route"));
    }





//    //web layer integrtaion test request -> controller-> exception handling -> response
//    @Test
//    void testGetRoutes_WhenServiceThrowsException_ReturnsBadRequest() throws Exception {
//
//        // simulate service throwing an exception
//        when(routeService.getRoutes()).thenThrow(new RuntimeException("DB error"));
//
//        mockMvc.perform(get("/route"))
//                .andExpect(status().isBadRequest());
//    }


}

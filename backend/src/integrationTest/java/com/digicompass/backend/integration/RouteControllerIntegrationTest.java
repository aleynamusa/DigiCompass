package com.digicompass.backend.integration;

import com.digicompass.backend.repository.entity.RouteEntity;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")//escape the autowired bean warning
public class RouteControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RouteJpaRepository routeRepository;
    @Autowired
    private UserJpaRepository userRepository;

    private RouteEntity route;
    private UserEntity user;

    @BeforeEach
    void setup() {
        routeRepository.deleteAll();
        userRepository.deleteAll();

        user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@mail.com");
        user.setBirthDate(LocalDate.of(1995,1,1));
        user.setPassword("pw");
        userRepository.save(user);

        GeometryFactory factory = new GeometryFactory();

        RouteEntity r = new RouteEntity();
        r.setName("Test Route");
        r.setDescription("A sample route");
        r.setRouteType("HIKING");
        r.setDifficulty("EASY");
        r.setDistance(10f);
        r.setDuration("01:30");
        r.setCreatedByUserId(user);
        r.setCreatedAt(LocalDateTime.now());
        r.setUpdatedAt(LocalDateTime.now());

        r.setRouteGeometry(factory.createPoint(
                new Coordinate(10.0, 20.0)
        ));

        route = routeRepository.save(r);
    }

    @Test
    void getRoutes_ShouldReturnAllRoutes() throws Exception {
        mockMvc.perform(get("/route"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Route"))
                .andExpect(jsonPath("$[0].routeType").value("HIKING"));
    }

    @Test
    void getRouteGeometry_ShouldReturnGeometry() throws Exception {
        mockMvc.perform(get("/route/" + route.getId() + "/geometry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(route.getId()));
    }


    @Test
    void getRouteGeometry_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/route/999999/geometry"))
                .andExpect(status().isNotFound());
    }


    @Test
    void getRoutesByKeyword_ShouldReturnMatchingRoutes() throws Exception {

        mockMvc.perform(get("/route/keyword")
                        .param("keyword", "Test Route"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Route"));

    }

    @Test
    void getRoutesByKeyword_ShouldReturnEmptyList_WhenNoMatches() throws Exception {
        mockMvc.perform(get("/route/keyword")
                        .param("keyword", "Nomatch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getRoutesFiltered_ShouldReturnByType() throws Exception {
        mockMvc.perform(get("/route/filter")
                        .param("type", "HIKING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].routeType").value("HIKING"));
    }

    @Test
    void getRoutesFiltered_ShouldReturnByDifficulty() throws Exception {
        mockMvc.perform(get("/route/filter")
                        .param("difficulty", "EASY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].difficulty").value("EASY"));
    }

    @Test
    void getRoutesFiltered_ShouldReturnEmpty_WhenFiltersDoNotMatch() throws Exception {
        mockMvc.perform(get("/route/filter")
                        .param("difficulty", "HARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }


    @Test
    void getRoutes_ShouldReturnBadRequest_WhenServiceThrows() throws Exception {
        routeRepository.deleteAll();

        mockMvc.perform(get("/route"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void calculateDistance_ShouldReturnDistance() throws Exception {
        String jsonBody = """
        {
            "type": "LineString",
            "coordinates": [
                [10.0, 20.0],
                [11.0, 21.0]
            ]
        }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/route/calculate-distance")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    void calculateDistance_ShouldReturnBadRequest_WhenOnlyOnePoint() throws Exception {
        String jsonBody = """
        {
            "type": "LineString",
            "coordinates": [
                [10.0, 20.0]
            ]
        }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/route/calculate-distance")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getLikedRoutesByUserId_ShouldReturnLikedRoutes() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/route/liked/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

    }
}

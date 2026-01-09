package com.digicompass.backend.integration;

import com.digicompass.backend.configuration.UserPrincipal;
import com.digicompass.backend.repository.entity.RoleEntity;
import com.digicompass.backend.repository.entity.RouteEntity;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.RoleJpaRepository;
import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import com.digicompass.backend.types.Difficulty;
import com.digicompass.backend.types.RouteType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")//escape the autowired bean warning
public class RouteControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RouteJpaRepository routeRepository;
    @Autowired
    private UserJpaRepository userRepository;
    @Autowired
    private RoleJpaRepository roleRepository;

    private RouteEntity route;
    private RouteEntity route2;
    private UserEntity user;

    @BeforeEach
    void setup() {
        routeRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        RoleEntity role = new RoleEntity();
        role.setRole("USER");
        roleRepository.save(role);

        user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@mail.com");
        user.setBirthDate(LocalDate.of(1995, 1, 1));
        user.setRole(role);
        user.setPassword("pw");
        userRepository.saveAndFlush(user);

        GeometryFactory geometryFactory =
                new GeometryFactory(new PrecisionModel(), 4326);

        RouteEntity r = new RouteEntity();
        r.setName("Test Route");
        r.setDescription("A sample route");
        r.setRouteType(RouteType.Cycling);
        r.setDifficulty(Difficulty.Easy);
        r.setDistance(10f);
        r.setDuration("1h 30m");
        r.setCreatedByUserId(user);

        r.setRouteGeometry(
                geometryFactory.createLineString(new Coordinate[]{
                        new Coordinate(10.0, 20.0),
                        new Coordinate(10.5, 20.5),
                        new Coordinate(11.0, 21.0)
                })
        );

        route = routeRepository.save(r);

        RouteEntity r2 = new RouteEntity();
        r2.setName("Test Route 2");
        r2.setDescription("A sample route 2");
        r2.setRouteType(RouteType.Cycling);
        r2.setDifficulty(Difficulty.Hard);
        r2.setDistance(15f);
        r2.setDuration("1h 30m");
        r2.setCreatedByUserId(user);

        r2.setRouteGeometry(
                geometryFactory.createLineString(new Coordinate[]{
                        new Coordinate(12.0, 22.0),
                        new Coordinate(12.3, 22.3)
                })
        );

        route2 = routeRepository.save(r2);
    }

    @Test
    void getRoutes_ShouldReturnAllRoutes() throws Exception {
        mockMvc.perform(get("/route"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))

                .andExpect(jsonPath("$[0].id").value(route.getId()))
                .andExpect(jsonPath("$[0].name").value("Test Route"))
                .andExpect(jsonPath("$[0].description").value("A sample route"))
                .andExpect(jsonPath("$[0].routeType").value("Cycling"))
                .andExpect(jsonPath("$[0].difficulty").value("Easy"))
                .andExpect(jsonPath("$[0].distance").value(10.0))
                .andExpect(jsonPath("$[0].duration").value("1h 30m"))

                .andExpect(jsonPath("$[0].createdByUserId.id").value(user.getId()))
                .andExpect(jsonPath("$[0].createdByUserId.username").value(user.getUsername()))

                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].updatedAt").exists())

                .andExpect(jsonPath("$[1].name").value("Test Route 2"))
                .andExpect(jsonPath("$[1].description").value("A sample route 2"))
                .andExpect(jsonPath("$[1].routeType").value("Cycling"))
                .andExpect(jsonPath("$[1].difficulty").value("Hard"))
                .andExpect(jsonPath("$[1].distance").value(15.0))
                .andExpect(jsonPath("$[1].duration").value("1h 30m"))
                .andExpect(jsonPath("$[1].createdByUserId.id").value(user.getId()))
                .andExpect(jsonPath("$[1].createdByUserId.username").value(user.getUsername()))
                .andExpect(jsonPath("$[1].createdAt").exists())
                .andExpect(jsonPath("$[1].updatedAt").exists())
        ;
    }


    @Test
    void getRouteGeometry_ShouldReturnGeometry() throws Exception {
        mockMvc.perform(get("/route/" + route.getId() + "/geometry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(route.getId()))
                .andExpect(jsonPath("$.name").value("Test Route"))
                .andExpect(jsonPath("$.geojson.type").value("LineString"))
                .andExpect(jsonPath("$.geojson.coordinates").isArray())
                .andExpect(jsonPath("$.geojson.coordinates[0][0]").value(10.0))
                .andExpect(jsonPath("$.geojson.coordinates[0][1]").value(20.0))
                .andExpect(jsonPath("$.geojson.coordinates[1][0]").value(10.5))
                .andExpect(jsonPath("$.geojson.coordinates[1][1]").value(20.5))
                .andExpect(jsonPath("$.geojson.coordinates[2][0]").value(11.0))
                .andExpect(jsonPath("$.geojson.coordinates[2][1]").value(21.0));
    }



    @Test
    void getRouteGeometry_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/route/999999/geometry"))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void getRoutesByKeyword_ShouldReturnMatchingRoutes() throws Exception {
        mockMvc.perform(get("/route/keyword")
                        .param("keyword", "Test Route 2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(route2.getId()))
                .andExpect(jsonPath("$[0].name").value("Test Route 2"))
                .andExpect(jsonPath("$[0].description").value("A sample route 2"))
                .andExpect(jsonPath("$[0].routeType").value("Cycling"))
                .andExpect(jsonPath("$[0].difficulty").value("Hard"))
                .andExpect(jsonPath("$[0].distance").value(15.0))
                .andExpect(jsonPath("$[0].duration").value("1h 30m"))
                .andExpect(jsonPath("$[0].createdByUserId.id").value(user.getId()))
                .andExpect(jsonPath("$[0].createdByUserId.username").value(user.getUsername()))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].updatedAt").exists());
    }


    @Test
    void getRoutesByKeyword_ShouldReturnEmptyList_WhenNoMatches() throws Exception {
        mockMvc.perform(get("/route/keyword")
                        .param("keyword", "Nomatch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }


    @Test
    void getRoutesFiltered_ShouldReturnByDifficulty() throws Exception {
        mockMvc.perform(get("/route/filter")
                        .param("difficulty", "EASY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].difficulty").value("Easy"))
                .andExpect(jsonPath("$[0].name").value("Test Route"))
                .andExpect(jsonPath("$[0].routeType").value("Cycling"));
    }


    @Test
    void getRoutesFiltered_ShouldReturnEmpty_WhenFiltersDoNotMatch() throws Exception {
        mockMvc.perform(get("/route/filter")
                        .param("difficulty", "MEDIUM"))
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
    void getLikedRoutesByUserId_ShouldReturnLikedRoutes() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/route/liked/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

    }

    @Test
    void deleteRoute_ShouldReturnNoContent_WhenAuthorized() throws Exception {
        UserPrincipal ownerPrincipal = new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/route/delete/" + route.getId())
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                ownerPrincipal,
                                                null,
                                                ownerPrincipal.getAuthorities()
                                        )
                                ))
                )
                .andExpect(status().isNoContent());

        assertFalse(routeRepository.findById(route.getId()).isPresent());
    }


    @Test
    void deleteRoute_ShouldReturnForbidden_WhenNotOwner() throws Exception {

        UserEntity otherUser = new UserEntity();
        otherUser.setUsername("other");
        otherUser.setEmail("other@mail.com");
        otherUser.setBirthDate(LocalDate.of(1990, 1, 1));
        otherUser.setPassword("pw");
        userRepository.saveAndFlush(otherUser);

        UserPrincipal otherPrincipal = new UserPrincipal(
                otherUser.getId(),
                otherUser.getUsername(),
                otherUser.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/route/delete/" + route.getId())
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                otherPrincipal,
                                                null,
                                                otherPrincipal.getAuthorities()
                                        )
                                ))
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void createRoute_ShouldReturnCreated() throws Exception {

        UserPrincipal principal = new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        mockMvc.perform(
                        multipart("/route/create")
                                .param("name", "New Route")
                                .param("description", "Test description")
                                .param("routeType", "Cycling")
                                .param("difficulty", "Easy")
                                .param("distance", "12.5")
                                .param("duration", "1h 15")
                                .param("routeGeometry", "LINESTRING (10 20, 15 25)") // or "POINT (10 20)" depending on controller
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                principal,
                                                null,
                                                principal.getAuthorities()
                                        )
                                ))
                )
                .andExpect(status().isCreated())
        ;
    }

}

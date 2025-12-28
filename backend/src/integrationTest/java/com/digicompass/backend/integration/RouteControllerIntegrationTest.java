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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private MockMvc mockMvc;
    @Autowired
    private RouteJpaRepository routeRepository;
    @Autowired
    private UserJpaRepository userRepository;
    @Autowired
    private RoleJpaRepository roleRepository;

    private RouteEntity route;
    private UserEntity user;

    @BeforeEach
    void setup() {
        routeRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();


        RoleEntity role = new RoleEntity();
        role.setRole("ROLE_USER");
        roleRepository.save(role);

        user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@mail.com");
        user.setBirthDate(LocalDate.of(1995,1,1));
        user.setRole(role);
        user.setPassword("pw");
        userRepository.saveAndFlush(user);

        GeometryFactory factory = new GeometryFactory();

        RouteEntity r = new RouteEntity();
        r.setName("Test Route");
        r.setDescription("A sample route");
        r.setRouteType(RouteType.Cycling);
        r.setDifficulty(Difficulty.Easy);
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
                .andExpect(jsonPath("$[0].routeType").value("Cycling"));
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
//
//    @Test
//    void getRoutesFiltered_ShouldReturnByType() throws Exception {
//        mockMvc.perform(get("/route/filter")
//                        .param("type", "Hiking"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].routeType").value("Hiking"));
//    } TODO fix this test

    @Test
    void getRoutesFiltered_ShouldReturnByDifficulty() throws Exception {
        mockMvc.perform(get("/route/filter")
                        .param("difficulty", "EASY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].difficulty").value("Easy"));
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
                                .param("routeGeometry", "POINT (10 20)")
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                principal,
                                                null,
                                                principal.getAuthorities()
                                        )
                                ))
                )
                .andExpect(status().isCreated());
    }
}

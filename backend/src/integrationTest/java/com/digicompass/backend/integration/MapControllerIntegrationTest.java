package com.digicompass.backend.integration;

import com.digicompass.backend.controller.dto.PointDto;
import com.digicompass.backend.controller.dto.request.RouteRequestMapDto;
import com.digicompass.backend.controller.mapper.MapMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.flywaydb.core.internal.util.JsonUtils.toJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import java.util.List;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class MapControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void routeInfoTest_Throws() throws Exception {
        RouteRequestMapDto request = new RouteRequestMapDto(
                List.of(
                        new PointDto(48.8566, 2.3522),
                        new PointDto(48.8570, 2.3530)
                ),
                "walk"
        );

        mockMvc.perform(post("/map")
                        .contentType("application/json")
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distanceKm").value(0.245477))
                .andExpect(jsonPath("$.durationHour").value("3m"))
                .andExpect(jsonPath("$.route").isArray());
    }

    @Test
    void routeInfo_shouldReturnInternalServerError_whenLessThanTwoPoints() throws Exception {
        RouteRequestMapDto request = new RouteRequestMapDto(
                List.of(new PointDto(48.8566, 2.3522)),
                "walk"
        );

        mockMvc.perform(post("/map")
                        .contentType("application/json")
                        .content(toJson(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void routeInfo_shouldReturnBadRequest_nullPoints() throws Exception {
        RouteRequestMapDto request = new RouteRequestMapDto(
                List.of(),
                "walk"
        );

        mockMvc.perform(post("/map")
                        .contentType("application/json")
                        .content(toJson(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getCurrentLocation_ShouldReturnCityName() throws Exception {
        double latitude = 48.8566;
        double longitude = 2.3522;

        mockMvc.perform(get("/map/coords")
                        .param("latitude", String.valueOf(latitude))
                        .param("longitude", String.valueOf(longitude)))
                .andExpect(status().isOk())
                .andExpect(content().string("Paris"));
    }


}

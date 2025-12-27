package com.digicompass.backend.integration;

import com.digicompass.backend.application.interfaces.GraphHopperService;
import com.digicompass.backend.controller.dto.PointDto;
import com.digicompass.backend.controller.dto.request.RouteRequestMapDto;
import com.digicompass.backend.controller.mapper.MapMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.flywaydb.core.internal.util.JsonUtils.toJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class MapControllerCatchIntegrationTest extends BaseIntegrationTest{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    GraphHopperService graphHopperServiceMock;

    @Autowired
    MapMapper mapMapperMock;

    @Test
    void routeInfo_catchesException() throws Exception {
        when(graphHopperServiceMock.calculateRoute(any(), any())).thenThrow(new RuntimeException("Forced exception"));

        mockMvc.perform(post("/map")
                        .contentType("application/json")
                        .content(toJson(new RouteRequestMapDto(
                                List.of(
                                        new PointDto(48.8566, 2.3522),
                                        new PointDto(48.8570, 2.3530)
                                ),
                                "walk"
                        ))))
                .andExpect(status().isInternalServerError());
    }
}

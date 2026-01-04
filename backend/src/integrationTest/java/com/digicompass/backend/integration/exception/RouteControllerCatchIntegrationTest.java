package com.digicompass.backend.integration.exception;

import com.digicompass.backend.application.interfaces.RouteService;

import com.digicompass.backend.controller.mapper.RouteMapperController;
import com.digicompass.backend.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class RouteControllerCatchIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RouteService routeService;

    @Autowired
    private RouteMapperController routeMapperController;

    @Test
    void getRoutes_throwsException() throws Exception {
        when(routeService.getRoutes())
                .thenThrow(new IllegalArgumentException("Invalid input"));

        mockMvc.perform(get("/route"))
                .andExpect(status().isInternalServerError());
    }






}

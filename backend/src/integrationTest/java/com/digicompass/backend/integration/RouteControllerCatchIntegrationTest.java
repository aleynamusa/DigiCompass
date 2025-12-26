package com.digicompass.backend.integration;

import com.digicompass.backend.application.interfaces.RouteService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class RouteControllerCatchIntegrationTest extends BaseIntegrationTest{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RouteService routeService;





}

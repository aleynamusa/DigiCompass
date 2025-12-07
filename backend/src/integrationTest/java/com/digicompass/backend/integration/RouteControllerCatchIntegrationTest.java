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

    @Test
    void calculateDistance_ShouldReturnInternalError_WhenInvalidJson() throws Exception {
        String invalidJson = "{ \"type\": \"LineString\", \"coordinates\": \"wrong format\" }";


        Mockito.doThrow(new IllegalArgumentException("Invalid GeoJSON provided."))
                    .when(routeService)
                    .calculateDistanceFromGeoJson(null);



        mockMvc.perform(MockMvcRequestBuilders.post("/route/calculate-distance")
                        .contentType("application/json")
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}

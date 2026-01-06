package com.digicompass.backend.integration;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class WeatherRecommendationControllerIntegrationTest extends BaseIntegrationTest {


    @Test
    void getRecommendations_ShouldReturnRecommendations() throws Exception {
        mockMvc.perform(get("/weather/recommendations")
                        .param("lat", "40.7128")
                        .param("lon", "-74.0060"))
                .andExpect(status().isOk());
    }


}

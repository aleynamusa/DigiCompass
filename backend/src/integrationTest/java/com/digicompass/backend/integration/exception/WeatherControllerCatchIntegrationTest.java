package com.digicompass.backend.integration.exception;

import com.digicompass.backend.application.interfaces.WeatherService;
import com.digicompass.backend.integration.BaseIntegrationTest;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class WeatherControllerCatchIntegrationTest  extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherService weatherService;

    @Test
    void getCurrentWeather_CatchesJsonProcessingException() throws Exception {
        doThrow(new InvalidFormatException(null, "Invalid format", "value", String.class)) //subclass of JsonProcessingException
                .when(weatherService).fetchWeatherCurrent(anyDouble(), anyDouble());

        mockMvc.perform(MockMvcRequestBuilders.get("/weather/current")
                        .param("latitude", "52.3676")
                        .param("longitude", "4.9041")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error processing weather data"));
    }

    @Test
    void getHourlyWeather_CatchesJsonProcessingException() throws Exception {
        doThrow(new InvalidFormatException(null, "Invalid format", "value", String.class))
                .when(weatherService).fetchWeatherHourly(anyDouble(), anyDouble());

        mockMvc.perform(MockMvcRequestBuilders.get("/weather/hourly")
                        .param("latitude", "52.3676")
                        .param("longitude", "4.9041")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error processing weather data"));
    }

    @Test
    void getDailyWeather_CatchesJsonProcessingException() throws Exception {
        doThrow(new InvalidFormatException(null, "Invalid format", "value", String.class))
                .when(weatherService).fetchWeatherDaily(anyDouble(), anyDouble());

        mockMvc.perform(MockMvcRequestBuilders.get("/weather/daily")
                        .param("latitude", "52.3676")
                        .param("longitude", "4.9041")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error processing weather data"));
    }
}

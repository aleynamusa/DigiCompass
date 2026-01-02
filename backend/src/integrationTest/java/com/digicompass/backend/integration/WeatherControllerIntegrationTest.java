package com.digicompass.backend.integration;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class WeatherControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    static MockWebServer mockWeatherApi;

    @DynamicPropertySource
    static void overrideWeatherApi(DynamicPropertyRegistry registry) throws IOException {
        mockWeatherApi = new MockWebServer();
        mockWeatherApi.start();

        registry.add(
                "weather.open-meteo.base-url",
                () -> mockWeatherApi.url("/").toString()
        );
    }

    @AfterAll
    static void shutdown() throws IOException {
        mockWeatherApi.shutdown();
    }

    @Test
    void getGeoInfo_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/weather/location")
                        .param("name", "Paris"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].name").value("Paris"))
                .andExpect(jsonPath("$.results[0].latitude").value(48.85341))
                .andExpect(jsonPath("$.results[0].longitude").value(2.3488));
    }


    @Test
    void getCurrentWeather_ShouldReturnOk() throws Exception {

        mockWeatherApi.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setBody("""
            {
              "current": {
                "temperature_2m": 18.5,
                "relative_humidity_2m": 55,
                "apparent_temperature": 18.0,
                "weather_code": 1,
                "wind_speed_10m": 3.2
              }
            }
            """)
        );

        mockMvc.perform(get("/weather/current")
                        .param("latitude", "48.85341")
                        .param("longitude", "2.3488"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.temperature").value(18.5))
                .andExpect(jsonPath("$.condition").value("Mostly Clear"))
                .andExpect(jsonPath("$.windSpeed").value(3.2));
    }

    @Test
    void getHourlyWeather_ShouldReturnOk() throws Exception {
        mockWeatherApi.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setBody("""
                                {
                                                            "hourly": {
                                                              "time": ["2026-01-02T00:00"],
                                                              "temperature_2m": [18.5],
                                                              "weather_code": [1],
                                                              "uv_index": [1],
                                                              "precipitation_probability": [32]
                                                            }
                                                          }
        """)
        );

        mockMvc.perform(get("/weather/hourly")
                        .param("latitude", "48.85341")
                        .param("longitude", "2.3488"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.temperature").value(18.5))
                .andExpect(jsonPath("$.weatherCode").value("Mostly Clear"))
                .andExpect(jsonPath("$.uvIndex").value(1));

    }

    @Test
    void getDailyWeather_ShouldReturnOk() throws Exception {
        mockWeatherApi.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setBody("""
            {
              "daily": {
                "time": ["2026-01-02T00:00"],
                "temperature_2m_max": [18.5],
                "temperature_2m_min": [13.5],
                "weather_code": [1],
                "precipitation_probability_max": [1]
              }
            }
            """)
        );

        mockMvc.perform(get("/weather/daily")
                        .param("latitude", "48.85341")
                        .param("longitude", "2.3488"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxTemperature").value(18.5))
                .andExpect(jsonPath("$.minTemperature").value(13.5))
                .andExpect(jsonPath("$.weatherCode").value("Mostly Clear"))
                .andExpect(jsonPath("$.precipitation").value(1));

    }


}

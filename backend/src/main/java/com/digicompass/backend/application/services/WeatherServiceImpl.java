package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.WeatherService;
import com.digicompass.backend.application.models.weather.CurrentWeather;
import com.digicompass.backend.application.models.weather.GeoLocation;
import com.digicompass.backend.application.models.weather.GeoLocationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    private final WebClient webClientLocation;
    private final WebClient webClientWeather;


    @Autowired
    public WeatherServiceImpl(
            WebClient.Builder builder,
            @Value("${geocoding.open-meteo.base-url}") String locationBaseUrl,
            @Value("${weather.open-meteo.base-url}") String weatherBaseUrl
    ) {
        this.webClientLocation = builder.baseUrl(locationBaseUrl).build();
        this.webClientWeather = builder.baseUrl(weatherBaseUrl).build();
    }

    @Override
    public GeoLocationResponse fetchLocation(String name) {

        log.info("[SERVICE] Fetching location info from Geocoding for city: {}", name);

        return webClientLocation.get()
                .uri(uriBuilder -> uriBuilder.queryParam("name", name)
                        .queryParam("count", 1).build())
                .retrieve()
                .bodyToMono(GeoLocationResponse.class)
                .block();
    }

    @Override
    public String fetchWeatherHourly(double latitude, double longitude) {
        log.info("[SERVICE] Fetching forecast");

        return webClientWeather.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam(
                                "hourly",
                                "temperature_2m,precipitation,rain,wind_speed_10m," +
                                        "wind_speed_80m,wind_speed_120m,wind_speed_180m," +
                                        "apparent_temperature,showers,snowfall,weather_code," +
                                        "uv_index"
                        )
                        .queryParam("timezone", "auto")
                        .queryParam("forecast_days", 7)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Override
    public String fetchWeatherDaily(double latitude, double longitude) {
        log.info("[SERVICE] Fetching forecast");

        return webClientWeather.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam(
                                "daily",
                                "temperature_2m_max,temperature_2m_min,sunrise,sunset," +
                                        "rain_sum,snowfall_sum,precipitation_probability_max," +
                                        "wind_speed_10m_max,uv_index_max,temperature_2m_mean," +
                                        "cloud_cover_mean,weather_code"
                        )
                        .queryParam("timezone", "auto")
                        .queryParam("forecast_days", 7)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Override
    public CurrentWeather fetchWeatherCurrent(double latitude, double longitude) throws JsonProcessingException {
        log.info("[SERVICE] Fetching current weather forecast");

        JsonNode root = webClientWeather.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam(
                                "current",
                                "temperature_2m,relative_humidity_2m,apparent_temperature,rain,showers," +
                                        "snowfall,wind_speed_10m,precipitation,weather_code,cloud_cover,wind_gusts_10m,wind_direction_10m"
                        )
                        .queryParam("timezone", "auto")
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return new ObjectMapper()
                .treeToValue(root.get("current"), CurrentWeather.class);
    }



}

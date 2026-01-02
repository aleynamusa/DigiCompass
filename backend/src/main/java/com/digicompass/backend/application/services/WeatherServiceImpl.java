package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.WeatherService;
import com.digicompass.backend.application.models.weather.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    private final WebClient webClientLocation;
    private final WebClient webClientWeather;
    private final RedisTemplate<String, Object> redisTemplate;

    private final String latitudeStr = "latitude";
    private final String longitudeStr = "longitude";
    private final String timezoneStr = "timezone";


    @Autowired
    public WeatherServiceImpl(
            WebClient.Builder builder,
            @Value("${geocoding.open-meteo.base-url}") String locationBaseUrl,
            @Value("${weather.open-meteo.base-url}") String weatherBaseUrl, RedisTemplate<String, Object> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
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
    public HourlyWeather fetchWeatherHourly(double latitude, double longitude) throws JsonProcessingException {
        String key = "weather:hourly:" + latitude + ":" + longitude;
        log.info("[SERVICE] Fetching hourly forecast");

        try {
            HourlyWeather cached = (HourlyWeather) redisTemplate.opsForValue().get(key);
            if (cached != null) {
                log.info("[CACHE HIT] Returning cached hourly weather");
                return cached;
            }
        } catch (Exception e) {
            log.warn("[CACHE ERROR] Unable to get hourly weather from Redis: {}", e.getMessage());
        }

        try{
            JsonNode root =  webClientWeather.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam(latitudeStr, latitude)
                            .queryParam(longitudeStr, longitude)
                            .queryParam(
                                    "hourly",
                                    "temperature_2m,weather_code,uv_index,precipitation_probability"
                            )
                            .queryParam(timezoneStr, "auto")
                            .queryParam("forecast_days", 2)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            log.info("[SERVICE] Fetched hourly weather from API");

            HourlyWeather hourlyWeather= new ObjectMapper()
                    .treeToValue(root.get("hourly"), HourlyWeather.class);

            try {
                redisTemplate.opsForValue().set(key, hourlyWeather, Duration.ofHours(1));
            } catch (Exception e) {
                log.warn("[CACHE ERROR] Unable to set hourly weather in Redis: {}", e.getMessage());
            }


            return hourlyWeather;
        } catch(Exception e){
            log.error("[SERVICE] Error fetching hourly weather: {}", e.getMessage());
            throw new RuntimeException("Error fetching hourly weather", e);
        }
    }

    @Override
    public DailyWeather fetchWeatherDaily(double latitude, double longitude) throws JsonProcessingException {
        String key = "weather:daily:" + latitude + ":" + longitude;
        log.info("[SERVICE] Fetching daily forecast");

        try {
            DailyWeather cached = (DailyWeather) redisTemplate.opsForValue().get(key);
            if (cached != null) {
                log.info("[CACHE HIT] Returning cached daily weather");
                return cached;
            }
        } catch (Exception e) {
            log.warn("[CACHE ERROR] Unable to get daily weather from Redis: {}", e.getMessage());
        }

        try {
            JsonNode root = webClientWeather.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam(latitudeStr, latitude)
                            .queryParam(longitudeStr, longitude)
                            .queryParam(
                                    "daily",
                                    "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max"
                            )
                            .queryParam(timezoneStr, "auto")
                            .queryParam("forecast_days", 16)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            log.info("[SERVICE] Fetched daily weather from API");

            DailyWeather dailyWeather = new ObjectMapper()
                    .treeToValue(root.get("daily"), DailyWeather.class);

            try {
                redisTemplate.opsForValue().set(key, dailyWeather, Duration.ofDays(1));
            } catch (Exception e) {
                log.warn("[CACHE ERROR] Unable to set daily weather in Redis: {}", e.getMessage());
            }

            return dailyWeather;

        } catch (Exception e) {
            log.error("[SERVICE ERROR] Error fetching daily weather: {}", e.getMessage());
            throw new RuntimeException("Error fetching daily weather", e);
        }
    }


    @Override
    public CurrentWeather fetchWeatherCurrent(double latitude, double longitude) throws JsonProcessingException {
        String key = "weather:current:" + latitude + ":" + longitude;
        log.info("[SERVICE] Fetching current weather forecast");

        try {
            CurrentWeather cached = (CurrentWeather) redisTemplate.opsForValue().get(key);
            if (cached != null) {
                log.info("[CACHE HIT] Returning cached current weather");
                return cached;
            }
        } catch (Exception e) {
            log.warn("[CACHE ERROR] Unable to get current weather from Redis: {}", e.getMessage());
        }

        try {
            JsonNode root = webClientWeather.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam(latitudeStr, latitude)
                            .queryParam(longitudeStr, longitude)
                            .queryParam(
                                    "current",
                                    "temperature_2m,relative_humidity_2m,apparent_temperature,rain,showers," +
                                            "snowfall,wind_speed_10m,precipitation,weather_code,cloud_cover,wind_gusts_10m,wind_direction_10m"
                            )
                            .queryParam(timezoneStr, "auto")
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            log.info("[SERVICE] Fetched current weather from API");

            CurrentWeather currentWeather = new ObjectMapper()
                    .treeToValue(root.get("current"), CurrentWeather.class);

            try {
                redisTemplate.opsForValue().set(key,currentWeather, Duration.ofMinutes(15));
            } catch (Exception e) {
                log.warn("[CACHE ERROR] Unable to set current weather in Redis: {}", e.getMessage());
            }

            return currentWeather;
        } catch (Exception e) {
            log.error("[SERVICE ERROR] Error fetching current weather: {}", e.getMessage());
            throw new RuntimeException("Error fetching current weather", e);
        }

    }
}

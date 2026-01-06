package com.digicompass.backend.repository.externalAPIs;

import com.digicompass.backend.application.models.weather.CurrentWeather;
import com.digicompass.backend.application.models.weather.DailyWeather;
import com.digicompass.backend.application.models.weather.GeoLocationResponse;
import com.digicompass.backend.application.models.weather.HourlyWeather;
import com.digicompass.backend.repository.entity.weather.CurrentWeatherEntity;
import com.digicompass.backend.repository.entity.weather.DailyWeatherEntity;
import com.digicompass.backend.repository.entity.weather.HourlyWeatherEntity;
import com.digicompass.backend.repository.interfaces.WeatherClient;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@Slf4j
public class WeatherGateway implements WeatherClient {
    private final WebClient webClientLocation;
    private final WebClient webClientWeather;


    private final String latitudeStr = "latitude";
    private final String longitudeStr = "longitude";
    private final String timezoneStr = "timezone";


    @Autowired
    public WeatherGateway(
            WebClient.Builder builder,
            @Value("${geocoding.open-meteo.base-url}") String locationBaseUrl,
            @Value("${weather.open-meteo.base-url}") String weatherBaseUrl
    ) {
        this.webClientLocation = builder.baseUrl(locationBaseUrl).build();
        this.webClientWeather = builder.baseUrl(weatherBaseUrl).build();
    }

//    @Override
//    public GeoLocationResponse getLocation(String name) {
//
//        log.info("[SERVICE] Fetching location info from Geocoding for city: {}", name);
//
//        return webClientLocation.get()
//                .uri(uriBuilder -> uriBuilder.queryParam("name", name)
//                        .queryParam("count", 1).build())
//                .retrieve()
//                .bodyToMono(GeoLocationResponse.class)
//                .block();
//    }

//
//
//    @Override
//    public DailyWeather fetchWeatherDaily(double latitude, double longitude) throws JsonProcessingException {
//        String key = "weather:daily:" + latitude + ":" + longitude;
//        log.info("[SERVICE] Fetching daily forecast");
//
//        try {
//            DailyWeather cached = (DailyWeather) redisTemplate.opsForValue().get(key);
//            if (cached != null) {
//                log.info("[CACHE HIT] Returning cached daily weather");
//                return cached;
//            }
//        } catch (Exception e) {
//            log.warn("[CACHE ERROR] Unable to get daily weather from Redis: {}", e.getMessage());
//        }
//
//        try {
//            JsonNode root = webClientWeather.get()
//                    .uri(uriBuilder -> uriBuilder
//                            .queryParam(latitudeStr, latitude)
//                            .queryParam(longitudeStr, longitude)
//                            .queryParam(
//                                    "daily",
//                                    "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max"
//                            )
//                            .queryParam(timezoneStr, "auto")
//                            .queryParam("forecast_days", 16)
//                            .build())
//                    .retrieve()
//                    .bodyToMono(JsonNode.class)
//                    .block();
//
//            log.info("[SERVICE] Fetched daily weather from API");
//
//            DailyWeather dailyWeather = new ObjectMapper()
//                    .treeToValue(root.get("daily"), DailyWeather.class);
//
//            try {
//                redisTemplate.opsForValue().set(key, dailyWeather, Duration.ofDays(1));
//            } catch (Exception e) {
//                log.warn("[CACHE ERROR] Unable to set daily weather in Redis: {}", e.getMessage());
//            }
//
//            return dailyWeather;
//
//        } catch (Exception e) {
//            log.error("[SERVICE ERROR] Error fetching daily weather: {}", e.getMessage());
//            throw new RuntimeException("Error fetching daily weather", e);
//        }
//    }


    @Override
    public HourlyWeatherEntity getHourly(double lat, double lon) {
        try{
            JsonNode root =  webClientWeather.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam(latitudeStr, lat)
                            .queryParam(longitudeStr, lon)
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

            HourlyWeatherEntity hourlyWeather= new ObjectMapper()
                    .treeToValue(root.get("hourly"), HourlyWeatherEntity.class);


            return hourlyWeather;
        } catch(Exception e){
            log.error("[SERVICE] Error fetching hourly weather: {}", e.getMessage());
            throw new RuntimeException("Error fetching hourly weather", e);
        }
    }

    @Override
    public DailyWeatherEntity getDaily(double lat, double lon) {
        try {
            JsonNode root = webClientWeather.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam(latitudeStr, lat)
                            .queryParam(longitudeStr, lon)
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

            DailyWeatherEntity dailyWeather = new ObjectMapper()
                    .treeToValue(root.get("daily"), DailyWeatherEntity.class);

            return dailyWeather;

        } catch (Exception e) {
            log.error("[SERVICE ERROR] Error fetching daily weather: {}", e.getMessage());
            throw new RuntimeException("Error fetching daily weather", e);
        }
    }

    @Override
    public CurrentWeatherEntity getCurrent(double lat, double lon) {

        try {
            JsonNode root = webClientWeather.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam(latitudeStr, lat)
                            .queryParam(longitudeStr, lon)
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

            CurrentWeatherEntity currentWeather = new ObjectMapper()
                    .treeToValue(root.get("current"), CurrentWeatherEntity.class);


            return currentWeather;
        } catch (Exception e) {
            log.error("[SERVICE ERROR] Error fetching current weather: {}", e.getMessage());
            throw new RuntimeException("Error fetching current weather", e);
        }
    }
}


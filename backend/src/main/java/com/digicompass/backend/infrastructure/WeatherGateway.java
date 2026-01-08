package com.digicompass.backend.infrastructure;


import com.digicompass.backend.infrastructure.interfaces.WeatherClient;
import com.digicompass.backend.infrastructure.objects.weather.CurrentWeatherObject;
import com.digicompass.backend.infrastructure.objects.weather.DailyWeatherObject;
import com.digicompass.backend.infrastructure.objects.weather.HourlyWeatherObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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


    @Override
    public HourlyWeatherObject getHourly(double lat, double lon) {
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

            HourlyWeatherObject hourlyWeather= new ObjectMapper()
                    .treeToValue(root.get("hourly"), HourlyWeatherObject.class);


            return hourlyWeather;
        } catch(Exception e){
            log.error("[SERVICE] Error fetching hourly weather: {}", e.getMessage());
            throw new RuntimeException("Error fetching hourly weather", e);
        }
    }

    @Override
    public DailyWeatherObject getDaily(double lat, double lon) {
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

            DailyWeatherObject dailyWeather = new ObjectMapper()
                    .treeToValue(root.get("daily"), DailyWeatherObject.class);

            return dailyWeather;

        } catch (Exception e) {
            log.error("[SERVICE ERROR] Error fetching daily weather: {}", e.getMessage());
            throw new RuntimeException("Error fetching daily weather", e);
        }
    }

    @Override
    public CurrentWeatherObject getCurrent(double lat, double lon) {

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

            CurrentWeatherObject currentWeather = new ObjectMapper()
                    .treeToValue(root.get("current"), CurrentWeatherObject.class);


            return currentWeather;
        } catch (Exception e) {
            log.error("[SERVICE ERROR] Error fetching current weather: {}", e.getMessage());
            throw new RuntimeException("Error fetching current weather", e);
        }
    }
}


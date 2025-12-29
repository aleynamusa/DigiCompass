package com.digicompass.backend.application.models.weather;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentWeather {

    private String time;

    @JsonProperty("temperature_2m")
    private double temperature;

    @JsonProperty("relative_humidity_2m")
    private int humidity;

    @JsonProperty("apparent_temperature")
    private double feelsLike;

    private double rain;
    private double precipitation;

    @JsonProperty("wind_speed_10m")
    private double windSpeed;

    @JsonProperty("wind_gusts_10m")
    private double windGusts;

    @JsonProperty("wind_direction_10m")
    private int windDirection;

    @JsonProperty("weather_code")
    private int weatherCode;

    @JsonProperty("cloud_cover")
    private int cloudCover;

    private static final String[] DIRECTIONS = {
            "N", "NE", "E", "SE", "S", "SW", "W", "NW"
    };

    @JsonIgnore
    public String getWindDirectionCompass() {
        return DIRECTIONS[(int) Math.round(((double) windDirection % 360) / 45) % 8];
    }

    @JsonIgnore
    public String getCondition() {
        return switch (weatherCode) {
            case 0 -> "Clear";
            case 1, 2 -> "Mostly Clear";
            case 3 -> "Cloudy";
            case 45, 48 -> "Fog";
            case 51, 53, 55 -> "Drizzle";
            case 61, 63, 65 -> "Rain";
            case 66, 67 -> "Freezing Rain";
            case 71, 73, 75 -> "Snow";
            case 77 -> "Snow Grains";
            case 80, 81, 82 -> "Rain Showers";
            case 85, 86 -> "Snow Showers";
            case 95 -> "Thunderstorm";
            case 96, 99 -> "Thunderstorm with Hail";
            default -> "Unknown";
        };
    }
}

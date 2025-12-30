package com.digicompass.backend.application.models.weather;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DailyWeather {
    private List<String> time;

    @JsonProperty("temperature_2m_max")
    private List<Double> maxTemperature;

    @JsonProperty("temperature_2m_min")
    private List<Double> minTemperature;

    @JsonProperty("weather_code")
    private List<Integer> weatherCode;

    @JsonProperty("precipitation_probability_max")
    private List<Integer> precipitation;

    @JsonIgnore
    public List<String> getConditions() {
        List<String> conditions = new ArrayList<>();
        if (weatherCode == null) {
            return conditions;
        }
        for (int code : weatherCode) {
            String condition = switch (code) {
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
            conditions.add(condition);
        }
        return conditions;
    }
}

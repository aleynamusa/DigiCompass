package com.digicompass.backend.controller.mapper;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WeatherCodeMapper {

    public String toCondition(int code) {
        return switch (code) {
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

    public List<String> toConditions(List<Integer> codes) {
        if (codes == null) return List.of();
        return codes.stream()
                .map(this::toCondition)
                .toList();
    }
}

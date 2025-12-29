package com.digicompass.backend.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentWeatherResponseDto {

    private String time;

    private double temperature;

    private int humidity;

    private double feelsLike;

    private double rain;
    private double precipitation;

    private double windSpeed;

    private double windGusts;

    private String windDirection;

    private String condition;

    private int cloudCover;
}

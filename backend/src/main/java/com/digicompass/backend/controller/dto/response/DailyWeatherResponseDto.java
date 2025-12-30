package com.digicompass.backend.controller.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class DailyWeatherResponseDto {
    private List<String> time;

    private List<Double> maxTemperature;

    private List<Double> minTemperature;

    private List<String> weatherCode;

    private List<Integer> precipitation;
}

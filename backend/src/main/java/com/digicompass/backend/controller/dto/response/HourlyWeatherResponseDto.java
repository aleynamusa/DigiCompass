package com.digicompass.backend.controller.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class HourlyWeatherResponseDto {
    private List<String> time;

    private List<Double> temperature;

    private List<String> weatherCode;

    private List<Integer> uvIndex;

    private List<Integer> precipitation;
}

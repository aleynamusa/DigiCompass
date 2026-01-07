package com.digicompass.backend.application.models.weather;

import lombok.Data;
import java.util.List;

@Data
public class DailyWeather {
    private List<String> time;

    private List<Double> maxTemperature;

    private List<Double> minTemperature;

    private List<Integer> weatherCode;

    private List<Integer> precipitation;

}

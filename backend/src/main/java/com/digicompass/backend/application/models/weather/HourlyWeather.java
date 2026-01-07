package com.digicompass.backend.application.models.weather;

import lombok.Data;
import java.util.List;

@Data
public class HourlyWeather {
    private List<String> time;

    private List<Double> temperature;

    private List<Integer> weatherCode;

    private List<Integer> uvIndex;

    private List<Integer> precipitation;

}

package com.digicompass.backend.application.models.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

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

}

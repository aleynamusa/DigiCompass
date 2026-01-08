package com.digicompass.backend.infrastructure.objects.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class HourlyWeatherObject {
    private List<String> time;

    @JsonProperty("temperature_2m")
    private List<Double> temperature;

    @JsonProperty("weather_code")
    private List<Integer> weatherCode;

    @JsonProperty("uv_index")
    private List<Integer> uvIndex;

    @JsonProperty("precipitation_probability")
    private List<Integer> precipitation;

}

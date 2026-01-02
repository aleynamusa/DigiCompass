package com.digicompass.backend.application.models.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class HourlyWeather {
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

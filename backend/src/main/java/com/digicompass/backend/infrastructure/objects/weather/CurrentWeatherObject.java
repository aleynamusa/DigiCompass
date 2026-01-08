package com.digicompass.backend.infrastructure.objects.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentWeatherObject {
    private String time;

    @JsonProperty("temperature_2m")
    private double temperature;

    @JsonProperty("relative_humidity_2m")
    private int humidity;

    @JsonProperty("apparent_temperature")
    private double feelsLike;

    private double rain;
    private double precipitation;

    @JsonProperty("wind_speed_10m")
    private double windSpeed;

    @JsonProperty("wind_gusts_10m")
    private double windGusts;

    @JsonProperty("wind_direction_10m")
    private int windDirection;

    @JsonProperty("weather_code")
    private int weatherCode;

    @JsonProperty("cloud_cover")
    private int cloudCover;
}

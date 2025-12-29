package com.digicompass.backend.application.interfaces;


import com.digicompass.backend.application.models.weather.CurrentWeather;
import com.digicompass.backend.application.models.weather.GeoLocationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;


@Service
public interface WeatherService {
    GeoLocationResponse fetchLocation(
            String name
    );
    String fetchWeatherHourly(double latitude, double longitude);

    String fetchWeatherDaily(double latitude, double longitude);


    CurrentWeather fetchWeatherCurrent(double latitude, double longitude) throws JsonProcessingException;
}

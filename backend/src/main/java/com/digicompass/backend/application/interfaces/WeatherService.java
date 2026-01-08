package com.digicompass.backend.application.interfaces;


import com.digicompass.backend.application.models.weather.CurrentWeather;
import com.digicompass.backend.application.models.weather.DailyWeather;
import com.digicompass.backend.application.models.weather.HourlyWeather;
import com.digicompass.backend.application.models.weather.RecommendedDay;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;


public interface WeatherService {

    HourlyWeather fetchWeatherHourly(double latitude, double longitude) throws JsonProcessingException;

    DailyWeather fetchWeatherDaily(double latitude, double longitude) throws JsonProcessingException;


    CurrentWeather fetchWeatherCurrent(double latitude, double longitude) throws JsonProcessingException;

    List<RecommendedDay> recommendDays(
            double latitude,
            double longitude);

}

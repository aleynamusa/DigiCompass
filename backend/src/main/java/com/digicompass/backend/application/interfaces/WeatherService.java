package com.digicompass.backend.application.interfaces;


import com.digicompass.backend.application.models.weather.CurrentWeather;
import com.digicompass.backend.application.models.weather.DailyWeather;
import com.digicompass.backend.application.models.weather.HourlyWeather;
import com.fasterxml.jackson.core.JsonProcessingException;


public interface WeatherService {

    HourlyWeather fetchWeatherHourly(double latitude, double longitude) throws JsonProcessingException;

    DailyWeather fetchWeatherDaily(double latitude, double longitude) throws JsonProcessingException;


    CurrentWeather fetchWeatherCurrent(double latitude, double longitude) throws JsonProcessingException;
}

package com.digicompass.backend.infrastructure.interfaces;


import com.digicompass.backend.infrastructure.objects.weather.CurrentWeatherObject;
import com.digicompass.backend.infrastructure.objects.weather.DailyWeatherObject;
import com.digicompass.backend.infrastructure.objects.weather.HourlyWeatherObject;

public interface WeatherClient {
    HourlyWeatherObject getHourly(double lat, double lon);
    DailyWeatherObject getDaily(double lat, double lon);
    CurrentWeatherObject getCurrent(double lat, double lon);
}

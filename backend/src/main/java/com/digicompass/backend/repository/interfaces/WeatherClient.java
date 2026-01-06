package com.digicompass.backend.repository.interfaces;


import com.digicompass.backend.repository.entity.weather.CurrentWeatherEntity;
import com.digicompass.backend.repository.entity.weather.DailyWeatherEntity;
import com.digicompass.backend.repository.entity.weather.HourlyWeatherEntity;

public interface WeatherClient {
    HourlyWeatherEntity getHourly(double lat, double lon);
    DailyWeatherEntity getDaily(double lat, double lon);
    CurrentWeatherEntity getCurrent(double lat, double lon);
}

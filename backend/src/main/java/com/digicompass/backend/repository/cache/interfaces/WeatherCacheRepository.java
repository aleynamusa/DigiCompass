package com.digicompass.backend.repository.cache.interfaces;



import com.digicompass.backend.repository.entity.weather.CurrentWeatherEntity;
import com.digicompass.backend.repository.entity.weather.DailyWeatherEntity;
import com.digicompass.backend.repository.entity.weather.HourlyWeatherEntity;

import java.util.Optional;

public interface WeatherCacheRepository {
    Optional<HourlyWeatherEntity> getHourlyWeather(double lat, double lon);
    void saveHourlyWeather(double lat, double lon, HourlyWeatherEntity weather);

    Optional<DailyWeatherEntity> getDailyWeather(double lat, double lon);
    void saveDailyWeather(double lat, double lon, DailyWeatherEntity weather);

    Optional<CurrentWeatherEntity> getCurrentWeather(double lat, double lon);
    void saveCurrentWeather(double lat, double lon, CurrentWeatherEntity weather);
}

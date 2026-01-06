package com.digicompass.backend.application.mapper;

import com.digicompass.backend.application.models.weather.CurrentWeather;
import com.digicompass.backend.application.models.weather.DailyWeather;
import com.digicompass.backend.application.models.weather.HourlyWeather;
import com.digicompass.backend.repository.entity.weather.CurrentWeatherEntity;
import com.digicompass.backend.repository.entity.weather.DailyWeatherEntity;
import com.digicompass.backend.repository.entity.weather.HourlyWeatherEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WeatherMapper {
    DailyWeather toDaily(DailyWeatherEntity daily);
    HourlyWeather toHourly(HourlyWeatherEntity daily);
    CurrentWeather toCurrent(CurrentWeatherEntity daily);
}

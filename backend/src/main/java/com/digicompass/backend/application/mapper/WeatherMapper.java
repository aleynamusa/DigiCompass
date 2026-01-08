package com.digicompass.backend.application.mapper;

import com.digicompass.backend.application.models.weather.CurrentWeather;
import com.digicompass.backend.application.models.weather.DailyWeather;
import com.digicompass.backend.application.models.weather.HourlyWeather;

import com.digicompass.backend.infrastructure.objects.weather.CurrentWeatherObject;
import com.digicompass.backend.infrastructure.objects.weather.DailyWeatherObject;
import com.digicompass.backend.infrastructure.objects.weather.HourlyWeatherObject;
import com.digicompass.backend.repository.entity.weather.CurrentWeatherEntity;
import com.digicompass.backend.repository.entity.weather.DailyWeatherEntity;
import com.digicompass.backend.repository.entity.weather.HourlyWeatherEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WeatherMapper {
    DailyWeatherEntity toDailyEntity(DailyWeather daily);
    HourlyWeatherEntity toHourlyEntity(HourlyWeather daily);
    CurrentWeatherEntity toCurrentEntity(CurrentWeather daily);
    DailyWeather toDailyObject(DailyWeatherObject daily);
    HourlyWeather toHourlyObject(HourlyWeatherObject daily);
    CurrentWeather toCurrentObject(CurrentWeatherObject daily);
    DailyWeather toDaily(DailyWeatherEntity daily);
    HourlyWeather toHourly(HourlyWeatherEntity daily);
    CurrentWeather toCurrent(CurrentWeatherEntity daily);
}

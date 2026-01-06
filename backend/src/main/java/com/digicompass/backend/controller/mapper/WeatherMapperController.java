package com.digicompass.backend.controller.mapper;

import com.digicompass.backend.application.models.weather.CurrentWeather;
import com.digicompass.backend.application.models.weather.DailyWeather;
import com.digicompass.backend.application.models.weather.GeoLocationResponse;
import com.digicompass.backend.application.models.weather.HourlyWeather;
import com.digicompass.backend.controller.dto.GeoLocationResponseDto;
import com.digicompass.backend.controller.dto.response.CurrentWeatherResponseDto;
import com.digicompass.backend.controller.dto.response.DailyWeatherResponseDto;
import com.digicompass.backend.controller.dto.response.HourlyWeatherResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = WeatherCodeMapper.class
)
public interface WeatherMapperController {

    GeoLocationResponseDto toDto(GeoLocationResponse geoLocation);
    @Mapping(
            target = "windDirection",
            expression = "java(currentWeather.getWindDirectionCompass())"
    )
    @Mapping(
            target = "condition",
            source = "weatherCode"
    )
    CurrentWeatherResponseDto toCurrentDto(CurrentWeather currentWeather);

    @Mapping(
            target = "weatherCode",
            source = "weatherCode"
    )
    HourlyWeatherResponseDto toHourlyDto(HourlyWeather hourlyWeather);

    @Mapping(
            target = "weatherCode",
            source = "weatherCode"
    )
    DailyWeatherResponseDto toDailyDto(DailyWeather dailyWeather);


}

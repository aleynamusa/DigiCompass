package com.digicompass.backend.controller.mapper;

import com.digicompass.backend.application.models.weather.CurrentWeather;
import com.digicompass.backend.application.models.weather.GeoLocationResponse;
import com.digicompass.backend.controller.dto.GeoLocationResponseDto;
import com.digicompass.backend.controller.dto.response.CurrentWeatherResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WeatherMapper {

    GeoLocationResponseDto toDto(GeoLocationResponse geoLocation);
    @Mapping(
            target = "windDirection",
            expression = "java(currentWeather.getWindDirectionCompass())"
    )
    @Mapping(
            target = "condition",
            expression = "java(currentWeather.getCondition())"
    )
    CurrentWeatherResponseDto toCurrentDto(CurrentWeather currentWeather);
}

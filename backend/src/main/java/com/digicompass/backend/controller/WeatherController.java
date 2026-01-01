package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.WeatherService;
import com.digicompass.backend.controller.dto.GeoLocationResponseDto;
import com.digicompass.backend.controller.dto.response.CurrentWeatherResponseDto;
import com.digicompass.backend.controller.dto.response.HourlyWeatherResponseDto;
import com.digicompass.backend.controller.mapper.WeatherMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;
    private final WeatherMapper weatherMapper;

    @Autowired
    public WeatherController(WeatherService weatherService, WeatherMapper weatherMapper) {
        this.weatherService = weatherService;
        this.weatherMapper = weatherMapper;
    }

    @GetMapping(value = "/location", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GeoLocationResponseDto> getGeoInfo(@NotBlank @RequestParam String name){
        return ResponseEntity.ok(weatherMapper.toDto(weatherService.fetchLocation(name)));
    }


    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CurrentWeatherResponseDto> getCurrentWeather(
            @RequestParam double latitude,
            @RequestParam double longitude) throws JsonProcessingException {

        log.info("[CONTROLLER] Fetching current weather info.");
        return ResponseEntity.ok(weatherMapper.toCurrentDto(weatherService.fetchWeatherCurrent(latitude, longitude)));
    }


    @GetMapping(value = "/hourly", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HourlyWeatherResponseDto> getHourlyWeather(@RequestParam(required = true) double latitude,
                                                                     @RequestParam(required = true) double longitude) throws JsonProcessingException{

        log.info("[CONTROLLER] Fetching hourly weather info.");
        return ResponseEntity.ok(weatherMapper.toHourlyDto(weatherService.fetchWeatherHourly(latitude, longitude)));

    }

    @GetMapping(value = "/daily", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDailyWeather(@RequestParam(required = true) double latitude,
                                                                     @RequestParam(required = true) double longitude) throws JsonProcessingException{

        log.info("[CONTROLLER] Fetching daily weather info.");
        return ResponseEntity.ok(weatherMapper.toDailyDto(weatherService.fetchWeatherDaily(latitude, longitude)));

    }
}

package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.WeatherScoringService;
import com.digicompass.backend.application.interfaces.WeatherService;
import com.digicompass.backend.application.mapper.WeatherMapper;
import com.digicompass.backend.application.models.weather.*;
import com.digicompass.backend.infrastructure.interfaces.WeatherClient;
import com.digicompass.backend.repository.cache.interfaces.WeatherCacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    private final WeatherClient weatherClient;
    private final WeatherCacheRepository weatherCacheRepository;
    private final WeatherMapper weatherMapper;
    private final WeatherScoringService scoringService;

    public WeatherServiceImpl(
            WeatherClient weatherClient,
            WeatherCacheRepository weatherCacheRepository,
            WeatherMapper weatherMapper, WeatherScoringService scoringService) {
        this.weatherClient = weatherClient;
        this.weatherCacheRepository = weatherCacheRepository;
        this.weatherMapper = weatherMapper;
        this.scoringService = scoringService;
    }

    @Override
    public HourlyWeather fetchWeatherHourly(double lat, double lon) {
        log.info("[SERVICE] Fetching hourly weather for {},{}", lat, lon);

        return weatherCacheRepository.getHourlyWeather(lat, lon)
                .map(weatherMapper::toHourly)
                .orElseGet(() -> {
                    log.info("[SERVICE] Cache miss - fetching from Weather API");
                    try {
                        HourlyWeather weather =
                                weatherMapper.toHourlyObject(
                                        weatherClient.getHourly(lat, lon)
                                );

                        weatherCacheRepository.saveHourlyWeather(
                                lat, lon,
                                weatherMapper.toHourlyEntity(weather)
                        );

                        return weather;
                    } catch (Exception e) {
                        log.error("[SERVICE] Error fetching hourly weather: {}", e.getMessage());
                        throw new RuntimeException("Error fetching hourly weather", e);
                    }
                });
    }


    @Override
    public DailyWeather fetchWeatherDaily(double lat, double lon) {
        log.info("[SERVICE] Fetching daily weather for {},{}", lat, lon);

        return weatherCacheRepository.getDailyWeather(lat, lon)
                .map(weatherMapper::toDaily)
                .orElseGet(() -> {
                    log.info("[SERVICE] Cache miss - fetching from Weather API");
                    try {
                        DailyWeather weather = weatherMapper.toDailyObject(weatherClient.getDaily(lat, lon));
                        weatherCacheRepository.saveDailyWeather(lat, lon, weatherMapper.toDailyEntity(weather));
                        return weather;
                    } catch (Exception e) {
                        log.error("[SERVICE] Error fetching daily weather: {}", e.getMessage());
                        throw new RuntimeException("Error fetching daily weather", e);
                    }
                });
    }

    @Override
    public CurrentWeather fetchWeatherCurrent(double lat, double lon) {
        log.info("[SERVICE] Fetching current weather for {},{}", lat, lon);

        return weatherCacheRepository.getCurrentWeather(lat, lon)
                .map(weatherMapper::toCurrent)
                .orElseGet(() -> {
                    log.info("[SERVICE] Cache miss - fetching from Weather API");
                    try {
                        CurrentWeather weather = weatherMapper.toCurrentObject(weatherClient.getCurrent(lat, lon));
                        weatherCacheRepository.saveCurrentWeather(lat, lon, weatherMapper.toCurrentEntity(weather));
                        return weather;
                    } catch (Exception e) {
                        log.error("[SERVICE] Error fetching current weather: {}", e.getMessage());
                        throw new RuntimeException("Error fetching current weather", e);
                    }
                });
    }

    @Override
    public List<RecommendedDay> recommendDays(
            double latitude,
            double longitude
    ){

        DailyWeather daily = fetchWeatherDaily(latitude, longitude);

        List<RecommendedDay> results = new ArrayList<>();

        for (int i = 0; i < daily.getTime().size(); i++) {
            results.add(
                    scoringService.scoreDay(
                            daily.getTime().get(i),
                            daily.getWeatherCode().get(i),
                            daily.getMaxTemperature().get(i),
                            daily.getMinTemperature().get(i),
                            daily.getPrecipitation().get(i))
            );
        }

        return results;
    }
}
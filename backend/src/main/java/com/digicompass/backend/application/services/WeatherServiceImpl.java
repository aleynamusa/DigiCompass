package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.WeatherService;
import com.digicompass.backend.application.mapper.WeatherMapper;
import com.digicompass.backend.application.models.weather.*;
import com.digicompass.backend.repository.interfaces.WeatherClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    private final WeatherClient weatherClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final WeatherMapper weatherMapper;

    public WeatherServiceImpl(WeatherClient weatherClient, RedisTemplate<String, Object> redisTemplate, WeatherMapper weatherMapper) {
        this.weatherClient = weatherClient;
        this.redisTemplate = redisTemplate;
        this.weatherMapper = weatherMapper;
    }

    @Override
    public HourlyWeather fetchWeatherHourly(double lat, double lon) {
        String key = "weather:hourly:" + lat + ":" + lon;
        log.info("[SERVICE] Fetching hourly forecast");

        try {
            HourlyWeather cached = (HourlyWeather) redisTemplate.opsForValue().get(key);
            if (cached != null) {
                log.info("[CACHE HIT] Returning cached hourly weather");
                return cached;
            }
        } catch (Exception e) {
            log.warn("[CACHE ERROR] Unable to get hourly weather from Redis: {}", e.getMessage());
        }

        try{
            HourlyWeather hourlyWeather =  weatherMapper.toHourly(weatherClient.getHourly(lat, lon));
            log.info("[SERVICE] Fetched hourly weather from API");

            try {
                redisTemplate.opsForValue().set(key, hourlyWeather, Duration.ofHours(1));
            } catch (Exception e) {
                log.warn("[CACHE ERROR] Unable to set hourly weather in Redis: {}", e.getMessage());
            }

            return hourlyWeather;
        } catch(Exception e){
            log.error("[SERVICE] Error fetching hourly weather: {}", e.getMessage());
            throw new RuntimeException("Error fetching hourly weather", e);
        }
    }


    @Override
    public DailyWeather fetchWeatherDaily(double lat, double lon) {
        String key = "weather:daily:" + lat + ":" + lon;
        log.info("[SERVICE] Fetching daily forecast");

        try {
            DailyWeather cached = (DailyWeather) redisTemplate.opsForValue().get(key);
            if (cached != null) {
                log.info("[CACHE HIT] Returning cached daily weather");
                return cached;
            }
        } catch (Exception e) {
            log.warn("[CACHE ERROR] Unable to get daily weather from Redis: {}", e.getMessage());
        }

        try{
            DailyWeather dailyWeather =  weatherMapper.toDaily(weatherClient.getDaily(lat, lon));
            log.info("[SERVICE] Fetched daily weather from API");

            try {
                redisTemplate.opsForValue().set(key, dailyWeather, Duration.ofDays(1));
            } catch (Exception e) {
                log.warn("[CACHE ERROR] Unable to set daily weather in Redis: {}", e.getMessage());
            }

            return dailyWeather;
        } catch(Exception e){
            log.error("[SERVICE] Error fetching daily weather: {}", e.getMessage());
            throw new RuntimeException("Error fetching daily weather", e);
        }
    }

    @Override
    public CurrentWeather fetchWeatherCurrent(double lat, double lon) {
        String key = "weather:current:" + lat + ":" + lon;
        log.info("[SERVICE] Fetching current weather forecast");

        try {
            CurrentWeather cached = (CurrentWeather) redisTemplate.opsForValue().get(key);
            if (cached != null) {
                log.info("[CACHE HIT] Returning cached current weather");
                return cached;
            }
        }
        catch (Exception e) {
                log.warn("[CACHE ERROR] Unable to get current weather from Redis: {}", e.getMessage());
        }

        try {
            CurrentWeather currentWeather = weatherMapper.toCurrent(weatherClient.getCurrent(lat, lon));

            log.info("[SERVICE] Fetched current weather from API");

            try {
                    redisTemplate.opsForValue().set(key,currentWeather, Duration.ofMinutes(15));
            } catch (Exception e) {
                log.warn("[CACHE ERROR] Unable to set current weather in Redis: {}", e.getMessage());
            }

            return currentWeather;
        } catch (Exception e) {
            log.error("[SERVICE ERROR] Error fetching current weather: {}", e.getMessage());
            throw new RuntimeException("Error fetching current weather", e);
        }
    }
}
